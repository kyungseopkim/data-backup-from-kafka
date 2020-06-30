package com.lucidmotors.data.backfill

import java.util.Properties

class KafkaOptions(backfillOptions: BackfillOptions) extends Properties {
  put("bootstrap.servers", backfillOptions.brokers)
  put("key.deserializer", classOf[org.apache.kafka.common.serialization.StringDeserializer])
  put("value.deserializer", classOf[org.apache.kafka.common.serialization.StringDeserializer])
  put("auto.offset.reset", backfillOptions.kafkaOffset)
  put("enable.auto.commit", "false")
  put("group.id", backfillOptions.groupId)
  put("security.protocol", backfillOptions.protocol)

}
