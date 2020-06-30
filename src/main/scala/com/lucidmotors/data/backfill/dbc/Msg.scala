package com.lucidmotors.data.backfill.dbc

case class Msg(id: Int, description:Option[String], is_extended_frame: Boolean, name:String, length: Int, signals: Array[Sig] )
