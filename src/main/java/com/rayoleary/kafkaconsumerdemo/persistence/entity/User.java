package com.rayoleary.kafkaconsumerdemo.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Users")
public class User {

   @Id
   private String id;

   @Indexed(unique = true)
   private String userId;

   private String userName;

   public User(String userId, String userName) {
      this.userId = userId;
      this.userName = userName;
   }
}
