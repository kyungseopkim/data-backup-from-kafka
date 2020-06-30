package com.lucidmotors.data.backfill.decode

import java.io.{ByteArrayInputStream, DataInputStream}

import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream

class MessageStream(data : Array[Byte])
  extends DataInputStream (new DataInputStream(new BlockLZ4CompressorInputStream(new ByteArrayInputStream(data)))){

  @throws[java.io.EOFException]
  @throws[java.io.IOError]
  def getData(len: Int): Array[Byte] = {
    val data = new Array[Byte](len)
    ( 0 until len).foreach( data.update(_, readByte()))
    data
  }
}
