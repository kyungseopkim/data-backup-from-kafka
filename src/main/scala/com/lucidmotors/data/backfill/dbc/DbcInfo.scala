package com.lucidmotors.data.backfill.dbc

import net.liftweb.json
import net.liftweb.json.DefaultFormats

import scala.io.Source

case class DbcInfo(arxml: ArxmlInfo) {
  implicit val formats = DefaultFormats
  val resource = getClass.getClassLoader.getResourceAsStream(arxml.name)
  val input = Source.fromInputStream(resource).mkString("")
  val messages = json.parse(input).extract[Messages]
  val lookup :Map[Int, Msg] = messages.messages.map((m :Msg) =>  {
    val ss = m.signals.sortBy(_.start_bit)
    val newMsg :Msg = new Msg(m.id, m.description, m.is_extended_frame, m.name, m.length, ss)
    (m.id , newMsg)
  } ).toMap
}
