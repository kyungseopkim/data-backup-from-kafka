package com.lucidmotors.data.backfill.dbc

class DbcLookup(ref: Map[String, Map[Int, Msg]]) {
  def getDbc(vin:String): Map[Int, Msg] = {
    if (ref.contains(vin))
      return ref(vin)

    if (! ref.contains("default"))
      throw new IllegalArgumentException("Arxml Mapping File need default value")
    ref("default")
  }
}
