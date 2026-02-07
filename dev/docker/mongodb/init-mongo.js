// MongoDB initialization script
// This script creates the Users collection if it doesn't already exist

db = db.getSiblingDB('kafkaconsumerdemo');

// Create the Users collection if it doesn't exist
if (!db.getCollectionNames().includes('Users')) {
  db.createCollection('Users', {
    validator: {
      $jsonSchema: {
        bsonType: "object",
        required: ["userId", "userName"],
        properties: {
          userId: {
            bsonType: "string",
            description: "User ID - must be a string and is required"
          },
          userName: {
            bsonType: "string",
            description: "User name - must be a string and is required"
          }
        }
      }
    }
  });

  // Create a unique index on userId to ensure upsert works correctly
  db.Users.createIndex({"userId": 1}, {unique: true});

  print("Users collection created successfully with schema validation and unique index on userId");
} else {
  print("Users collection already exists");
}
