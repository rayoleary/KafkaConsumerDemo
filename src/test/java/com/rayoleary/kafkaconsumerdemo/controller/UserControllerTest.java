package com.rayoleary.kafkaconsumerdemo.controller;

import com.rayoleary.kafkaconsumerdemo.messaging.dto.DemoEventPayload;
import com.rayoleary.kafkaconsumerdemo.persistence.entity.User;
import com.rayoleary.kafkaconsumerdemo.persistence.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

   @Mock
   private UserService userService;

   @InjectMocks
   private UserController userController;

   private DemoEventPayload validPayload;
   private User savedUser;

   @BeforeEach
   void setUp() {
      validPayload = new DemoEventPayload("john_doe", "user123");
      savedUser = new User("id123", "user123", "john_doe");
   }

   @Test
   void testUpsertUser_ReturnsOkWithUser_WhenValidPayload() {
      when(userService.upsertUser(any(DemoEventPayload.class))).thenReturn(savedUser);

      ResponseEntity<User> response = userController.upsertUser(validPayload);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getId()).isEqualTo("id123");
      assertThat(response.getBody().getUserId()).isEqualTo("user123");
      assertThat(response.getBody().getUserName()).isEqualTo("john_doe");

      verify(userService).upsertUser(validPayload);
   }

   @Test
   void testUpsertUser_WithDifferentValidPayload() {
      DemoEventPayload differentPayload = new DemoEventPayload("jane_doe", "user456");
      User differentUser = new User("id456", "user456", "jane_doe");
      when(userService.upsertUser(any(DemoEventPayload.class))).thenReturn(differentUser);

      ResponseEntity<User> response = userController.upsertUser(differentPayload);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getUserId()).isEqualTo("user456");
      assertThat(response.getBody().getUserName()).isEqualTo("jane_doe");

      verify(userService).upsertUser(differentPayload);
   }

   @Test
   void testUpsertUser_CallsServiceOnce() {
      when(userService.upsertUser(any(DemoEventPayload.class))).thenReturn(savedUser);

      userController.upsertUser(validPayload);

      verify(userService).upsertUser(validPayload);
   }
}
