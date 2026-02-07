# Kafka Consumer Demo

A Spring Boot application demonstrating Kafka message consumption with MongoDB persistence and REST API integration.

## Prerequisites

- Java 17
- Docker and Docker Compose
- Gradle

## Project Structure

- **Kafka Consumer**: Consumes messages from the `DEMO` topic and processes them via REST API
- **REST API**: Provides endpoints for user management (would typically be a separate RESTful API in it's own project)
- **MongoDB**: Stores user data with automatic collection initialization
- **Message Schema**: JSON-based `DemoEventPayload` with validation

## Dependencies

- Spring Boot 4.0.2
- Spring Boot Starter Web
- Spring Kafka
- Spring Data MongoDB
- Jackson (JSON processing)
- Jakarta Validation
- Lombok

## Setup

### 1. Start MongoDB

```bash
cd dev/docker/mongodb
docker-compose up -d
```

### 2. Start Kafka

```bash
cd dev/docker/kafka
docker-compose up -d
./produce-messages.sh
```

This will:

- Start Zookeeper on port 2181
- Start Kafka on port 9092
- Create the `DEMO` topic
- Publish 20 test messages

### 3. Run the Application

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## Configuration

### Local Profile (`application-local.yml`)

- **Kafka**: localhost:9092
    - Consumer group: `kafka-consumer-demo`
    - Manual acknowledgment enabled
    - JSON deserialization for `DemoEventPayload`

- **MongoDB**: localhost:27017
    - Database: `kafkaconsumerdemo`
    - Credentials: admin/admin123

## Message Schema

```json
{
  "userName": "string",
  "userId": "string"
}
```

Both fields are required and cannot be blank.

## Architecture

### Kafka Consumer Flow

1. `DemoTopicConsumer` receives messages from the `DEMO` topic
2. Sends HTTP POST request to `/api/users/upsert` endpoint with the payload
3. Acknowledges message only if HTTP response is 2xx (successful)
4. Messages with failed HTTP responses are not acknowledged and will be retried

### MongoDB Integration

- **Collection**: `Users` (automatically created on startup via `init-mongo.js`)
- **Schema Validation**: Enforces required fields for `userId` and `userName`
- **Unique Index**: `userId` field ensures no duplicate users
- **Repository**: Spring Data MongoDB repository for data access
- **Service**: Upsert logic - updates existing users or creates new ones based on `userId`

### REST API

**Endpoint**: `POST /api/users/upsert`

**Request Body**:

```json
{
  "userName": "john_doe",
  "userId": "user123"
}
```

**Response**: Returns the created/updated User entity with status 200 OK

**Validation**: Returns 400 Bad Request if userName or userId is blank/null

## Viewing MongoDB Data

### Option 1: MongoDB Shell

```bash
docker exec -it kafka-consumer-demo-mongodb mongosh -u admin -p admin123 --authenticationDatabase admin kafkaconsumerdemo
db.Users.find()
```

### Option 2: MongoDB Compass

Connection string: `mongodb://admin:admin123@localhost:27017/kafkaconsumerdemo?authSource=admin`

## Testing

Run all unit tests:

```bash
./gradlew test
```

Test coverage includes:

- User entity tests
- UserService upsert logic (create and update scenarios)
- UserController REST endpoint tests
- DemoTopicConsumer message processing (success and failure scenarios)
