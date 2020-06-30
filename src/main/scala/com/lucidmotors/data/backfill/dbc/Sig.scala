package com.lucidmotors.data.backfill.dbc

case class Sig(bit_length:Int, factor: Float, is_big_endian:Boolean, is_signed: Boolean, name:String, offset: Int, start_bit: Int, minimum: Option[Int], maximum: Option[Int], unit: Option[String], recv_nodes: Option[String], val_desc:Option[String])
