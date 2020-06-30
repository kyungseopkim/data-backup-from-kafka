package com.lucidmotors.data.backfill.decode

import java.util

import com.lucidmotors.data.backfill.dbc.Sig

class DBCDataHandler(payload: Array[Byte], signal: Sig) {

  def slice() : (Array[Byte], Int) = {
    if (payload.length <= 8) {
      (payload, signal.start_bit)
    } else {
      val index = signal.start_bit  / 64
      val window = index * 8
      val startBit = signal.start_bit - (window * 8)
      (payload.slice(window, window+8), startBit)
    }
  }

  val (data:Array[Byte], startBit:Int) = slice()

  val flattenBinary = getBitArray(signal.is_big_endian)

  private def reverse(bytes: Array[Byte]):Array[Byte] = bytes.toList.reverse.toArray

  private def getBitArray(bigEndian:Boolean): util.BitSet =  if (bigEndian) util.BitSet.valueOf(reverse(data))
  else util.BitSet.valueOf(data)

  private def getData(range: Seq[Int]): util.BitSet = {
    val data: util.BitSet = new util.BitSet(signal.bit_length)
    var count = 0
    for (i <- range) {
      data.set(count, flattenBinary.get(i))
      count += 1
    }
    data
  }
  private def getLongValue(range:Seq[Int]):Long = {
    val ll = getData(range).toLongArray
    if (ll.length>0) ll(0) else 0
  }

  private def littleEndianValue():Long = {
    getLongValue(startBit until (startBit + signal.bit_length))
  }

  private def bigEndianValue():Long = {
    val adj = 64 - startBit - signal.bit_length
    val bitRange = (adj until (adj + signal.bit_length))
    getLongValue(bitRange)
  }

  def getValue(): Float = {
    val value = if (signal.is_big_endian) bigEndianValue() else littleEndianValue()
    val vv = (value * signal.factor) + signal.offset
    vv.toFloat
  }
}
