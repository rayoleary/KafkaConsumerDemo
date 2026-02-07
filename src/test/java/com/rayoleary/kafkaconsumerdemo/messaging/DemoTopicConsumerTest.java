package com.rayoleary.kafkaconsumerdemo.messaging;

import com.rayoleary.kafkaconsumerdemo.messaging.dto.DemoEventPayload;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DemoTopicConsumerTest {

   @Mock
   private Acknowledgment acknowledgment;

   @InjectMocks
   private DemoTopicConsumer demoTopicConsumer;

   private ConsumerRecord<String, DemoEventPayload> consumerRecord;
   private DemoEventPayload payload;

   @BeforeEach
   void setUp() {
      payload = new DemoEventPayload("john_doe", "user123");
      consumerRecord = new ConsumerRecord<>("DEMO", 0, 0L, "key", payload);
   }

   @Test
   void testOnMessage_SuccessfulProcessing() {
      demoTopicConsumer.onMessage(consumerRecord, acknowledgment);

      verify(acknowledgment, times(1)).acknowledge();
   }

   @Test
   void testOnMessage_WithDifferentPayload() {
      DemoEventPayload differentPayload = new DemoEventPayload("jane_doe", "user456");
      ConsumerRecord<String, DemoEventPayload> differentRecord =
            new ConsumerRecord<>("DEMO", 0, 1L, "key", differentPayload);

      demoTopicConsumer.onMessage(differentRecord, acknowledgment);

      verify(acknowledgment, times(1)).acknowledge();
   }

   @Test
   void testOnMessage_WithNullKey() {
      ConsumerRecord<String, DemoEventPayload> recordWithNullKey =
            new ConsumerRecord<>("DEMO", 0, 0L, null, payload);

      demoTopicConsumer.onMessage(recordWithNullKey, acknowledgment);

      verify(acknowledgment, times(1)).acknowledge();
   }

   @Test
   void testOnMessage_MultipleMessages() {
      demoTopicConsumer.onMessage(consumerRecord, acknowledgment);

      DemoEventPayload secondPayload = new DemoEventPayload("alice", "user789");
      ConsumerRecord<String, DemoEventPayload> secondRecord =
            new ConsumerRecord<>("DEMO", 0, 1L, "key2", secondPayload);
      demoTopicConsumer.onMessage(secondRecord, acknowledgment);

      verify(acknowledgment, times(2)).acknowledge();
   }
}
