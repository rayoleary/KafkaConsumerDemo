package com.rayoleary.kafkaconsumerdemo.messaging;

import com.rayoleary.kafkaconsumerdemo.messaging.dto.DemoEventPayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DemoTopicConsumer {

   @KafkaListener(
         topics = "${spring.kafka.consumer.topic}",
         groupId = "${spring.kafka.consumer.group-id}",
         containerFactory = "kafkaListenerContainerFactory"
   )
   public void onMessage(
         ConsumerRecord<String, DemoEventPayload> record,
         Acknowledgment ack) {

      log.info("Received message: {}", record.value());
      ack.acknowledge();
      log.info("Acknowledged message: {}", record.value());
   }
}
