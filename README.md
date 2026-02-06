# Kafka Consumer Demo

A Spring Boot application demonstrating Kafka message consumption with MongoDB integration.

## Prerequisites

- Java 17
- Docker and Docker Compose
- Gradle

## Project Structure

- **Kafka Consumer**: Consumes messages from the `DEMO` topic
- **MongoDB**: Stores consumed messages
- **Message Schema**: JSON-based `DemoEventPayload` with validation

## Dependencies

- Spring Boot 4.0.2
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

## Consumer Details

The `DemoTopicConsumer` listens to the `DEMO` topic with:

- Manual message acknowledgment
- JSON deserialization
- Validation constraints on payload fields
