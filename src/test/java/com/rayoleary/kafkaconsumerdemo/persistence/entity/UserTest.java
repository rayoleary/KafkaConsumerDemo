package com.rayoleary.kafkaconsumerdemo.persistence.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

   @Test
   void testUserCreationWithAllFields() {
      User user = new User("id123", "user456", "john_doe");

      assertThat(user.getId()).isEqualTo("id123");
      assertThat(user.getUserId()).isEqualTo("user456");
      assertThat(user.getUserName()).isEqualTo("john_doe");
   }

   @Test
   void testUserCreationWithoutId() {
      User user = new User("user456", "john_doe");

      assertThat(user.getId()).isNull();
      assertThat(user.getUserId()).isEqualTo("user456");
      assertThat(user.getUserName()).isEqualTo("john_doe");
   }

   @Test
   void testNoArgsConstructor() {
      User user = new User();

      assertThat(user.getId()).isNull();
      assertThat(user.getUserId()).isNull();
      assertThat(user.getUserName()).isNull();
   }

   @Test
   void testSetters() {
      User user = new User();
      user.setId("id123");
      user.setUserId("user456");
      user.setUserName("john_doe");

      assertThat(user.getId()).isEqualTo("id123");
      assertThat(user.getUserId()).isEqualTo("user456");
      assertThat(user.getUserName()).isEqualTo("john_doe");
   }

   @Test
   void testEqualsAndHashCode() {
      User user1 = new User("id123", "user456", "john_doe");
      User user2 = new User("id123", "user456", "john_doe");
      User user3 = new User("id456", "user789", "jane_doe");

      assertThat(user1).isEqualTo(user2);
      assertThat(user1).hasSameHashCodeAs(user2);
      assertThat(user1).isNotEqualTo(user3);
   }

   @Test
   void testToString() {
      User user = new User("id123", "user456", "john_doe");

      String toString = user.toString();

      assertThat(toString).contains("id123");
      assertThat(toString).contains("user456");
      assertThat(toString).contains("john_doe");
   }
}
