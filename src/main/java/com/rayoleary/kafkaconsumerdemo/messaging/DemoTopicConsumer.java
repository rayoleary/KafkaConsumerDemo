package com.rayoleary.kafkaconsumerdemo.messaging;

import com.rayoleary.kafkaconsumerdemo.messaging.dto.DemoEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoTopicConsumer {

   private final RestClient.Builder restClientBuilder;

   @KafkaListener(
         topics = "${spring.kafka.consumer.topic}",
         groupId = "${spring.kafka.consumer.group-id}",
         containerFactory = "kafkaListenerContainerFactory"
   )
   public void onMessage(
         ConsumerRecord<String, DemoEventPayload> record,
         Acknowledgment ack) {

      log.info("Received message: {}", record.value());

      RestClient restClient = restClientBuilder.baseUrl("http://localhost:8080").build();
      ResponseEntity responseEntity = restClient.post()
            .uri("/api/users/upsert")
            .body(record.value())
            .retrieve()
            .toBodilessEntity();

      if (responseEntity.getStatusCode().is2xxSuccessful()) {
         ack.acknowledge();
         log.info("Acknowledged message: {}", record.value());
      } else {
         log.error("Failed to process message: {}", record.value());
      }
   }
}
