package com.lucidmotors.data.backfill.decode

import java.io.{EOFException, IOException}

class Packet(vin:String, seqCounter:Int, vlan:Int, ts: Long, nano: Int, msgCount: Int, is: MessageStream) {

  @throws[EOFException]
  @throws[IOException]
  def getMessages(): Array[Message] = {
    val result = new Array[Message](msgCount)
    for( i <- 0 until msgCount) {
        result.update(i, getMsg())
    }

    return result
  }

  @throws[java.io.EOFException]
  @throws[java.io.IOException]
  private def getMsg() : Message = {
    val msgId = is.readInt()
    val msgLen = is.readInt()
    val payload = is.getData(msgLen)
    new Message(vin, seqCounter, vlan, ts, nano, msgId, payload)
  }
}
