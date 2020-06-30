package com.lucidmotors.data.backfill.decode

case class Message(vin:String, seqCounter:Int, vlan:Int, timestamp: Long, usec: Int, msgId:Int, payload: Array[Byte])
