package com.lucidmotors.data.backfill.decode

@SerialVersionUID(1576179543)
case class Signal(msgId: Int, timestamp: Long, epoch: Int, usec: Int, vlan:Int, vin:String,
                  msgName: String, signalName: String, value: Float) extends Serializable
