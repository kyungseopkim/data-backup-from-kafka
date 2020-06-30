package com.lucidmotors.data.backfill

import java.util.Properties

class BackfillOptions extends Properties {
  val brokers = sys.env.getOrElse("BROKERS", "localhost:9092")
  val protocol = sys.env.getOrElse("PROTOCOL", "PLAINTEXT")
  val kafkaOffset = sys.env.getOrElse("KAFKA_OFFSET", "earliest")
  val groupId = sys.env.getOrElse("KAFKA_GROUP_ID", "data-backfill")
  val topic = sys.env.getOrElse("KAFKA_TOPIC", "raw_car_message_test")
  val outputDir = sys.env.getOrElse("OUTPUT_FOLDER", "s3a://redshift-live-vehicles-signals/scala-client/csv")
  val bufferSize = sys.env.getOrElse("BUFFER_SIZE", "500000")
}
