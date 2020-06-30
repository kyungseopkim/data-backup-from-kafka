package com.lucidmotors.data.backfill.decode

import java.io.{EOFException, IOException}
import java.nio.charset.StandardCharsets

import org.apache.log4j.Logger

import scala.collection.mutable.ArrayBuffer

class MessageBreaker(content: Array[Byte]) {
  @transient lazy val logger = Logger.getLogger(getClass)

  val is = new MessageStream(content)
  val v2_vehicles = getV2Vechicles

  private def getV2Vechicles(): Array[String] = {
    val vehicles = System.getenv("V2_VEHICLES")
    if (vehicles == null) return Array("000001")
    vehicles.split(",").map(_.trim)
  }
  case class MessageHead(vin: String, seq:Int= -1, vlan:Int= -1)

  private def getPacket(header:MessageHead): Packet = {
    try {
      val timestamp = is.readLong()
      val timestamp_nano = is.readInt()
      val msgCount = is.readInt()
      new Packet(header.vin, header.seq, header.vlan, timestamp, timestamp_nano, msgCount, is)
    } catch {
      case e : EOFException =>
        null
    }
  }

  private def getMessageV1(vin: String): MessageHead = MessageHead(vin)

  private def getMessageV2(vin: String): MessageHead = {
    val version:Int = is.readUnsignedByte()
    if (version !=2)
      throw new IllegalArgumentException(s"VIN ${vin} Version ${version} VERSION mismatched")
    val seqCounter: Int = is.readUnsignedByte()
    val vlan: Int = is.readUnsignedByte()
    MessageHead(vin, seqCounter, vlan)
  }

  private def getHeader(vin: String): MessageHead =
    if (v2_vehicles.contains(vin)) getMessageV2(vin) else getMessageV1(vin)

  def messages(): Array[Message] = {
    def header(): MessageHead = {
      try {
        val len = is.readByte().toInt
        val vin = new String(is.getData(len), StandardCharsets.UTF_8)
        getHeader(vin)
      } catch {
        case e: java.io.IOException =>
          logger.error("Data Chunk is corrupt. skip this chunk")
          null
      }
    }

    val vinHeader = header()

    val result: ArrayBuffer[Message] = ArrayBuffer()
    if (vinHeader == null)
      result.toArray

    var packet = getPacket(vinHeader)
    try {
      while (packet != null) {
        result ++= packet.getMessages()
        packet = getPacket(vinHeader)
      }
    } catch {
      case ex: IOException =>
        logger.error(ex)
    }
    result.toArray
  }

}
