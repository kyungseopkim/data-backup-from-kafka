package com.lucidmotors.data.backfill

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import net.liftweb.json._
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.IOUtils

case class KafkaMsg(clientid:String, username:String, topic:String, payload:String, qos: Int, node:String, ts:Long)

class KafkaMessage(packet: Array[Byte]) {
  implicit val formats = DefaultFormats
  private val content = new String(IOUtils.toByteArray(new ByteArrayInputStream(packet)),
    StandardCharsets.UTF_8)
  private val json = parse(content)
  val msg = json.extract[KafkaMsg]
  val payload = Base64.decodeBase64(msg.payload)
}
