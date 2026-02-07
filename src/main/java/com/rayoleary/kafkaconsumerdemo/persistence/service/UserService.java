package com.rayoleary.kafkaconsumerdemo.persistence.service;

import com.rayoleary.kafkaconsumerdemo.messaging.dto.DemoEventPayload;
import com.rayoleary.kafkaconsumerdemo.persistence.entity.User;
import com.rayoleary.kafkaconsumerdemo.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

   private final UserRepository userRepository;

   /**
    * Upserts a user to the Users collection based on the DemoEventPayload.
    * If a user with the given userId exists, it will be updated.
    * If no user exists, a new one will be created.
    *
    * @param payload the DemoEventPayload containing user information
    * @return the saved or updated User entity
    */
   public User upsertUser(DemoEventPayload payload) {
      log.debug("Upserting user with userId: {}", payload.getUserId());

      User user = userRepository.findByUserId(payload.getUserId())
            .map(existingUser -> {
               log.debug("Updating existing user with userId: {}", payload.getUserId());
               existingUser.setUserName(payload.getUserName());
               return existingUser;
            })
            .orElseGet(() -> {
               log.debug("Creating new user with userId: {}", payload.getUserId());
               return new User(payload.getUserId(), payload.getUserName());
            });

      User savedUser = userRepository.save(user);
      log.info("Successfully upserted user with userId: {}", savedUser.getUserId());

      return savedUser;
   }
}
