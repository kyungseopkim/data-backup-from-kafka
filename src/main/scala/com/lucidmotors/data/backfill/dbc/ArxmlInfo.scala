package com.lucidmotors.data.backfill.dbc

import scala.beans.BeanProperty

class ArxmlInfo {
  @BeanProperty var name: String = ""
  @BeanProperty var version: String = ""
  @BeanProperty var vin: Array[String] = Array()
  @BeanProperty var default: Boolean = false

  override def toString: String = s"${name}:${version}:${vin.mkString(",")}:${default}"
}
