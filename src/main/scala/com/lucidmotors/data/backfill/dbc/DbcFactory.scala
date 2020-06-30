package com.lucidmotors.data.backfill.dbc

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.collection.mutable

object DbcFactory {
  def init(): Map[String, Map[Int, Msg]] = {
    val mappingFileName = "arxml_mapping.yaml"
    val resource = getClass.getClassLoader.getResourceAsStream(mappingFileName)
    val yaml = new Yaml(new Constructor(classOf[ArxmlMappingFile]))
    val mapping = yaml.load(resource).asInstanceOf[ArxmlMappingFile]
    val dbcs: Array[DbcInfo] = mapping.arxml.map(new DbcInfo(_))
    val map = new mutable.HashMap[String, Map[Int, Msg]]()
    dbcs.foreach { (dbc: DbcInfo) =>
      dbc.arxml.vin.foreach { (vin: String) =>
        map.put(vin, dbc.lookup)
      }
      if (dbc.arxml.default)
        map.put("default", dbc.lookup)
    }
    map.toMap
  }
  val lookup = new DbcLookup(init())
}
