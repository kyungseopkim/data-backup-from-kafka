#!/bin/bash

export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
sbt clean assembly
VERSION=v0.1.0
IMG=411026478373.dkr.ecr.us-east-1.amazonaws.com/data-backfill-from-kafka:$VERSION
docker build -t $IMG .
docker push $IMG
