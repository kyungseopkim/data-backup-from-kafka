package com.lucidmotors.data.backfill

import java.net.URI
import java.nio.file.Paths
import java.time.{Duration, LocalDate, ZoneId}
import java.util.Collections
import java.util.concurrent.Executors

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.lucidmotors.data.backfill.dbc.DbcFactory
import com.lucidmotors.data.backfill.decode.{MessageDecoder, Signal}
import com.sksamuel.avro4s.{AvroOutputStream, AvroSchema}
import net.liftweb.json.DefaultFormats
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.log4j.LogManager

import scala.jdk.CollectionConverters._
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

case class BucketName(name:String, path:String)

class Writer(signals : Array[Signal], outputDir:String) extends Runnable {
  lazy val logger = LogManager.getLogger(getClass)
  def now(): String = System.currentTimeMillis().toString
  def today():String = LocalDate.now(ZoneId.of("UTC")).toString

  def getS3Client():AmazonS3 = {
    AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build()
  }

  val outputBucket = {
    val uri = new URI(outputDir)
    BucketName(uri.getHost, uri.getPath)
  }

  override def run(): Unit = {
    val filename:String = s"/tmp/${now()}.parquet"
    logger.info(s"writing to ${filename}")
    val outputFile = Paths.get(filename).toFile
    val schema = AvroSchema[Signal]
    val os = AvroOutputStream.data[Signal].to(outputFile).build(schema)
    os.write(signals)
    os.close()
    val client = getS3Client()

    val dst = Array(outputBucket.path.substring(1), today(), outputFile.getName).mkString("/")
    logger.info(s"upload to ${dst}")
    client.putObject(outputBucket.name, dst, outputFile)
    logger.info(s"s3://${Array(outputBucket.name, dst).mkString("/")} upload done")
    outputFile.delete()
  }
}

object KafkaListener extends App {
  lazy val logger = LogManager.getLogger(getClass)
  implicit val formats = DefaultFormats

  val lookup = DbcFactory.lookup
  val options = new BackfillOptions

  val bucketSize = options.bufferSize.toInt
  val buffer = new ArrayBuffer[Signal](bucketSize)
  val props = new KafkaOptions(options)

  def decode (content:Array[Byte]) : Array[Signal] = {
    new MessageDecoder(content, lookup).get()
  }

  val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
  consumer.subscribe(Collections.singletonList(options.topic))

  val service = Executors.newCachedThreadPool()

  while (true) {
    val results = consumer.poll(Duration.ofMillis(200)).asScala
    for (record <- results) {
      breakable {
        try {
          val payload = new KafkaMessage(record.value().getBytes).payload
          buffer ++= decode(payload)
        } catch {
          case e: Exception =>
            logger.error(e)
            break
        }
      }
      if (buffer.size >= bucketSize) {
        service.submit(new Writer(buffer.toArray.clone(), options.outputDir))
        buffer.clear()
        consumer.commitAsync()
      }
    }
  }
  consumer.close()
}
