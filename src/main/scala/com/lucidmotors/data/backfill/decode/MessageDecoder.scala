package com.lucidmotors.data.backfill.decode

import com.lucidmotors.data.backfill.dbc.{DbcLookup, Msg, Sig}


class MessageDecoder(content:Array[Byte], dbcLookup: DbcLookup) extends MessageBreaker (content) {

  def get():Array[Signal] = {
    super.messages().map(this.parse).flatten
  }

  def parse(message: Message): Array[Signal] = {
    val dbc: Map[Int, Msg ] = dbcLookup.getDbc(message.vin)

    if (dbc.contains(message.msgId)) {
      val msg: Msg = dbc.get(message.msgId).get
      if (msg.length != message.payload.length) {
        logger.error(s"discrepancy between message length and ARXML message length\n ${message}")
        return Array(Signal(message.msgId,milliseconds(message),message.timestamp.toInt,message.usec,
          message.vlan, message.vin,msg.name,"NONE",0))
      }
      return msg.signals.map(decode(message, msg.name,  _))
    } else {
      logger.warn("Not Found in ARXML :" + message)
    }
    Array(Signal(message.msgId,milliseconds(message),message.timestamp.toInt,message.usec,
      message.vlan, message.vin,"NONE","NONE",0))
  }

  private def milliseconds(message:Message):Long = (((message.timestamp * 1000000) + message.usec) / 1000).toLong

  private def decode(message:Message, msgName: String, signal: Sig): Signal = {
    val handler = new DBCDataHandler(message.payload, signal)
    val value = handler.getValue()
    new Signal(message.msgId, milliseconds(message), message.timestamp.toInt,
      message.usec, message.vlan, message.vin, msgName, signal.name, value.toFloat)
  }
}
