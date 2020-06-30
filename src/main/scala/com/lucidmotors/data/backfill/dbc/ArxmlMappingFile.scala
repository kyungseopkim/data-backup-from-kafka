package com.lucidmotors.data.backfill.dbc

import scala.beans.BeanProperty

class ArxmlMappingFile {
  @BeanProperty var arxml: Array[ArxmlInfo] = Array()
  override def toString: String = s"${arxml.mkString("\n")}"
}
