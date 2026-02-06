package com.rayoleary.kafkaconsumerdemo.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoEventPayload {

   @NotBlank(message = "User name cannot be blank")
   @JsonProperty("userName")
   private String userName;

   @NotBlank(message = "User ID cannot be blank")
   @JsonProperty("userId")
   private String userId;
}
