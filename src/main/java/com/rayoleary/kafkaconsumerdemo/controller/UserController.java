package com.rayoleary.kafkaconsumerdemo.controller;

import com.rayoleary.kafkaconsumerdemo.messaging.dto.DemoEventPayload;
import com.rayoleary.kafkaconsumerdemo.persistence.entity.User;
import com.rayoleary.kafkaconsumerdemo.persistence.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

   private final UserService userService;

   @PostMapping("/upsert")
   public ResponseEntity<User> upsertUser(@Valid @RequestBody DemoEventPayload payload) {
      log.info("Received upsert request for userId: {}", payload.getUserId());
      User user = userService.upsertUser(payload);
      return ResponseEntity.status(HttpStatus.OK).body(user);
   }
}
