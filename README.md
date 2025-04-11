# SMS Routing - Distributed System Simulation

This project simulates a decentralized SMS routing system, where mobile users send messages through a network of interconnected antennas. The system is implemented in Java using RabbitMQ as the messaging middleware.

## Overview

- Each antenna represents a physical zone and can forward messages to neighboring antennas.
- Each user connects to one antenna at a time and can:
    - Send SMS messages to other users
    - Move to a different antenna (simulated)
- All communication is done through a single RabbitMQ exchange (`sms_network`) using direct routing.

The system mimics real-world distributed networks where no central service is available to locate users, and messages are routed based on local information only.

## Technologies

- Java 8+
- RabbitMQ
- AMQP Client Library (`amqp-client`)

## How to Run

### 1. Start RabbitMQ

Make sure RabbitMQ is running locally on port `5672`.

### 2. Compile

Download the RabbitMQ Java client (`amqp-client-<version>.jar`) and add it to the classpath:

```bash
javac -cp .:amqp-client-<version>.jar *.java
```

### 3. Launch Antennas

Each antenna must be launched in a separate terminal or process:

```bash
java -cp .:amqp-client-<version>.jar AntennaLauncher A B C
```

This starts antenna `A` connected to neighbors `B` and `C`.

### 4. Launch Users

```bash
java -cp .:amqp-client-<version>.jar UserClient Alice A
```

This starts user Alice connected to antenna A.

## Example

1. Start three antennas: `A`, `B`, `C`
2. Start two users: `Alice` on `A`, `Bob` on `C`
3. Alice sends a message to Bob → the system routes it via antennas

## Design Assumptions

- Antennas have unique names and know their direct neighbors.
- Users are connected to one antenna at a time.
- No centralized user-location service is used.
- Messages are routed hop-by-hop using a TTL and `visited` set to avoid loops.
- Antennas and users all communicate via the same RabbitMQ exchange.

## Dependencies

- [RabbitMQ Java Client](https://www.rabbitmq.com/java-client.html)

## License

This project is for educational purposes. Licensed under MIT.

Made for the IDS (Distributed Systems) course at Université Grenoble Alpes.
