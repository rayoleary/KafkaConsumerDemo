package com.rayoleary.kafkaconsumerdemo.persistence.service;

import com.rayoleary.kafkaconsumerdemo.messaging.dto.DemoEventPayload;
import com.rayoleary.kafkaconsumerdemo.persistence.entity.User;
import com.rayoleary.kafkaconsumerdemo.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

   @Mock
   private UserRepository userRepository;

   @InjectMocks
   private UserService userService;

   private DemoEventPayload payload;

   @BeforeEach
   void setUp() {
      payload = new DemoEventPayload("john_doe", "user123");
   }

   @Test
   void testUpsertUser_CreatesNewUser_WhenUserDoesNotExist() {
      when(userRepository.findByUserId(payload.getUserId())).thenReturn(Optional.empty());
      when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
         User savedUser = invocation.getArgument(0);
         savedUser.setId("generatedId");
         return savedUser;
      });

      User result = userService.upsertUser(payload);

      assertThat(result).isNotNull();
      assertThat(result.getUserId()).isEqualTo("user123");
      assertThat(result.getUserName()).isEqualTo("john_doe");

      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).findByUserId("user123");
      verify(userRepository).save(userCaptor.capture());

      User capturedUser = userCaptor.getValue();
      assertThat(capturedUser.getUserId()).isEqualTo("user123");
      assertThat(capturedUser.getUserName()).isEqualTo("john_doe");
   }

   @Test
   void testUpsertUser_UpdatesExistingUser_WhenUserExists() {
      User existingUser = new User("existingId", "user123", "old_name");
      when(userRepository.findByUserId(payload.getUserId())).thenReturn(Optional.of(existingUser));
      when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

      User result = userService.upsertUser(payload);

      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo("existingId");
      assertThat(result.getUserId()).isEqualTo("user123");
      assertThat(result.getUserName()).isEqualTo("john_doe");

      verify(userRepository).findByUserId("user123");
      verify(userRepository).save(existingUser);
   }

   @Test
   void testUpsertUser_PreservesUserId_WhenUpdatingExistingUser() {
      User existingUser = new User("existingId", "user123", "old_name");
      when(userRepository.findByUserId("user123")).thenReturn(Optional.of(existingUser));
      when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

      DemoEventPayload updatePayload = new DemoEventPayload("updated_name", "user123");
      User result = userService.upsertUser(updatePayload);

      assertThat(result.getUserId()).isEqualTo("user123");
      assertThat(result.getUserName()).isEqualTo("updated_name");
      assertThat(result.getId()).isEqualTo("existingId");
   }

   @Test
   void testUpsertUser_CallsRepositorySaveOnce() {
      when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
      when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

      userService.upsertUser(payload);

      verify(userRepository, times(1)).save(any(User.class));
   }

   @Test
   void testUpsertUser_WithDifferentPayload() {
      DemoEventPayload differentPayload = new DemoEventPayload("jane_doe", "user456");
      when(userRepository.findByUserId("user456")).thenReturn(Optional.empty());
      when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

      User result = userService.upsertUser(differentPayload);

      assertThat(result.getUserId()).isEqualTo("user456");
      assertThat(result.getUserName()).isEqualTo("jane_doe");
   }
}
