#!/bin/bash

echo "Publishing 20 messages to DEMO topic..."

for i in {1..20}
do
  MESSAGE="{\"userName\":\"user${i}\",\"userId\":\"id-${i}\"}"
  echo "user${i}:${MESSAGE}" | kafka-console-producer --bootstrap-server localhost:9092 --topic DEMO --property "parse.key=true" --property "key.separator=:"
  echo "Published message $i: $MESSAGE"
done

echo "Successfully published 20 messages to DEMO topic"
