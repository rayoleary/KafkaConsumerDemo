package com.rayoleary.kafkaconsumerdemo.messaging.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DemoEventPayloadTest {

   private static Validator validator;

   @BeforeAll
   static void setUp() {
      try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
         validator = factory.getValidator();
      }
   }

   @Test
   void testValidPayload() {
      DemoEventPayload payload = new DemoEventPayload("john_doe", "user123");

      Set<ConstraintViolation<DemoEventPayload>> violations = validator.validate(payload);

      assertTrue(violations.isEmpty());
      assertEquals("john_doe", payload.getUserName());
      assertEquals("user123", payload.getUserId());
   }

   @Test
   void testBlankUserName() {
      DemoEventPayload payload = new DemoEventPayload("", "user123");

      Set<ConstraintViolation<DemoEventPayload>> violations = validator.validate(payload);

      assertFalse(violations.isEmpty());
      assertEquals(1, violations.size());
      assertTrue(violations.iterator().next().getMessage().contains("User name cannot be blank"));
   }

   @Test
   void testNullUserName() {
      DemoEventPayload payload = new DemoEventPayload(null, "user123");

      Set<ConstraintViolation<DemoEventPayload>> violations = validator.validate(payload);

      assertFalse(violations.isEmpty());
      assertEquals(1, violations.size());
   }

   @Test
   void testBlankUserId() {
      DemoEventPayload payload = new DemoEventPayload("john_doe", "");

      Set<ConstraintViolation<DemoEventPayload>> violations = validator.validate(payload);

      assertFalse(violations.isEmpty());
      assertEquals(1, violations.size());
      assertTrue(violations.iterator().next().getMessage().contains("User ID cannot be blank"));
   }

   @Test
   void testNullUserId() {
      DemoEventPayload payload = new DemoEventPayload("john_doe", null);

      Set<ConstraintViolation<DemoEventPayload>> violations = validator.validate(payload);

      assertFalse(violations.isEmpty());
      assertEquals(1, violations.size());
   }

   @Test
   void testNoArgsConstructor() {
      DemoEventPayload payload = new DemoEventPayload();

      assertNull(payload.getUserName());
      assertNull(payload.getUserId());
   }

   @Test
   void testSettersAndGetters() {
      DemoEventPayload payload = new DemoEventPayload();
      payload.setUserName("jane_doe");
      payload.setUserId("user456");

      assertEquals("jane_doe", payload.getUserName());
      assertEquals("user456", payload.getUserId());
   }

   @Test
   void testEqualsAndHashCode() {
      DemoEventPayload payload1 = new DemoEventPayload("john_doe", "user123");
      DemoEventPayload payload2 = new DemoEventPayload("john_doe", "user123");
      DemoEventPayload payload3 = new DemoEventPayload("jane_doe", "user456");

      assertEquals(payload1, payload2);
      assertEquals(payload1.hashCode(), payload2.hashCode());
      assertNotEquals(payload1, payload3);
   }
}
