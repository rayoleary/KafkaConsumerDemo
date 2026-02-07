package com.rayoleary.kafkaconsumerdemo.messaging;

import com.rayoleary.kafkaconsumerdemo.messaging.dto.DemoEventPayload;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.web.client.RestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemoTopicConsumerTest {

   @Mock
   private Acknowledgment acknowledgment;

   @Mock
   private RestClient.Builder restClientBuilder;

   @Mock
   private RestClient restClient;

   @Mock
   private RestClient.RequestBodyUriSpec requestBodyUriSpec;

   @Mock
   private RestClient.RequestBodySpec requestBodySpec;

   @Mock
   private RestClient.ResponseSpec responseSpec;

   @Mock
   private ResponseEntity<Void> responseEntity;

   @InjectMocks
   private DemoTopicConsumer demoTopicConsumer;

   private ConsumerRecord<String, DemoEventPayload> consumerRecord;
   private DemoEventPayload payload;

   @BeforeEach
   void setUp() {
      payload = new DemoEventPayload("john_doe", "user123");
      consumerRecord = new ConsumerRecord<>("DEMO", 0, 0L, "key", payload);

      // Setup RestClient mock chain with lenient stubbing
      lenient().when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
      lenient().when(restClientBuilder.build()).thenReturn(restClient);
      lenient().when(restClient.post()).thenReturn(requestBodyUriSpec);
      lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
      lenient().when(requestBodySpec.body(any(DemoEventPayload.class))).thenReturn(requestBodySpec);
      lenient().when(requestBodySpec.retrieve()).thenReturn(responseSpec);
      lenient().when(responseSpec.toBodilessEntity()).thenReturn(responseEntity);

      // Setup successful response
      HttpStatusCode successStatus = HttpStatus.OK;
      lenient().when(responseEntity.getStatusCode()).thenReturn(successStatus);
   }

   @Test
   void testOnMessage_SuccessfulProcessing() {
      demoTopicConsumer.onMessage(consumerRecord, acknowledgment);

      verify(restClientBuilder).baseUrl("http://localhost:8080");
      verify(restClient).post();
      verify(requestBodyUriSpec).uri("/api/users/upsert");
      verify(requestBodySpec).body(payload);
      verify(acknowledgment, times(1)).acknowledge();
   }

   @Test
   void testOnMessage_WithDifferentPayload() {
      DemoEventPayload differentPayload = new DemoEventPayload("jane_doe", "user456");
      ConsumerRecord<String, DemoEventPayload> differentRecord =
            new ConsumerRecord<>("DEMO", 0, 1L, "key", differentPayload);

      demoTopicConsumer.onMessage(differentRecord, acknowledgment);

      verify(requestBodySpec).body(differentPayload);
      verify(acknowledgment, times(1)).acknowledge();
   }

   @Test
   void testOnMessage_WithNullKey() {
      ConsumerRecord<String, DemoEventPayload> recordWithNullKey =
            new ConsumerRecord<>("DEMO", 0, 0L, null, payload);

      demoTopicConsumer.onMessage(recordWithNullKey, acknowledgment);

      verify(requestBodySpec).body(payload);
      verify(acknowledgment, times(1)).acknowledge();
   }

   @Test
   void testOnMessage_MultipleMessages() {
      demoTopicConsumer.onMessage(consumerRecord, acknowledgment);

      DemoEventPayload secondPayload = new DemoEventPayload("alice", "user789");
      ConsumerRecord<String, DemoEventPayload> secondRecord =
            new ConsumerRecord<>("DEMO", 0, 1L, "key2", secondPayload);
      demoTopicConsumer.onMessage(secondRecord, acknowledgment);

      verify(restClient, times(2)).post();
      verify(acknowledgment, times(2)).acknowledge();
   }

   @Test
   void testOnMessage_DoesNotAcknowledge_WhenHttpResponseIsNot2xx() {
      HttpStatusCode errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      when(responseEntity.getStatusCode()).thenReturn(errorStatus);

      demoTopicConsumer.onMessage(consumerRecord, acknowledgment);

      verify(restClientBuilder).baseUrl("http://localhost:8080");
      verify(restClient).post();
      verify(requestBodyUriSpec).uri("/api/users/upsert");
      verify(requestBodySpec).body(payload);
      verify(acknowledgment, never()).acknowledge();
   }

   @Test
   void testOnMessage_DoesNotAcknowledge_WhenHttpResponseIs4xx() {
      HttpStatusCode clientErrorStatus = HttpStatus.BAD_REQUEST;
      when(responseEntity.getStatusCode()).thenReturn(clientErrorStatus);

      demoTopicConsumer.onMessage(consumerRecord, acknowledgment);

      verify(requestBodySpec).body(payload);
      verify(acknowledgment, never()).acknowledge();
   }
}
