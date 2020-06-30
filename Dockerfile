FROM openjdk:8-alpine

WORKDIR /root
COPY target/scala-2.12/data-backfill-from-kafka-assembly-0.1.0.jar /root

ENTRYPOINT ["java", "-jar", "/root/data-backfill-from-kafka-assembly-0.1.0.jar"]
