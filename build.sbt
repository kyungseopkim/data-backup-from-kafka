
scalaVersion     := "2.12.10"
version          := "0.1.0"

name := "data-backfill-from-kafka"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "2.4.0",
  "net.liftweb" %% "lift-json" % "3.4.0",
  "log4j" % "log4j" % "1.2.17",
  "com.amazonaws" % "aws-java-sdk" % "1.11.717",
  "org.apache.parquet" % "parquet-avro" % "1.11.0",
  "org.apache.parquet" % "parquet-hadoop" % "1.11.0",
  "com.sksamuel.avro4s" %% "avro4s-core" % "3.0.4",
  "org.yaml" % "snakeyaml" % "1.25",
  "org.apache.commons" % "commons-io" % "1.3.2",
  "log4j" % "log4j" % "1.2.17"
)

mainClass in assembly := Some("com.lucidmotors.data.backfill.KafkaListener")

assemblyMergeStrategy in assembly := {
  case PathList("com","fasterxml", "jackson", xs@_*) => MergeStrategy.last
  case "module-info.class" => MergeStrategy.last
  case "META-INF/io.netty.versions.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
