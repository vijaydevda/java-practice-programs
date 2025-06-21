Great! Let's begin learning **Apache Kafka** step by step, with enough theory and code examples. We'll break this into digestible parts.

---

## 🔹 **Part 1: Kafka Overview & Core Concepts**

### ✅ What is Apache Kafka?

**Apache Kafka** is a **distributed streaming platform** used to:

- Publish and subscribe to streams of records.
    
- Store data in a fault-tolerant way.
    
- Process streams of data in real-time.
    

Originally developed at LinkedIn, Kafka is widely used for building **real-time data pipelines** and **event-driven systems**.

---

### ✅ Core Concepts

|Term|Description|
|---|---|
|**Producer**|Publishes messages to Kafka topics.|
|**Consumer**|Subscribes to topics and processes messages.|
|**Topic**|Logical channel to which records are sent.|
|**Partition**|Topics are split into partitions for parallelism.|
|**Broker**|Kafka server that stores data and serves clients.|
|**Consumer Group**|Group of consumers working together to consume from a topic.|

---

### 🧠 Real-World Analogy

- **Topic**: Like a TV channel
    
- **Producer**: A TV station broadcasting to the channel
    
- **Consumer**: Your TV that subscribes and plays the broadcast
    
- **Partition**: Segments of the channel, allowing multiple TVs to watch different parts
    

---

### ✅ Key Kafka Features

- High throughput and scalability
    
- Durable and persistent
    
- Distributed by design
    
- Supports real-time stream processing
    

---

### ✅ Kafka Architecture Diagram

```
[Producer] --> [Broker 1 | Broker 2 | Broker 3] --> [Consumer Group]
                    |               |
                  Topic A (Partition 0, 1, 2)
```

Each **partition** is an ordered, immutable sequence of records and acts as a commit log.

---

✅ That’s the foundational theory.

Would you like to continue to **Part 2: Kafka Setup & Basic Producer/Consumer in Java**?
Awesome! Let’s move on to:

---

## 🔹 **Part 2: Kafka Setup & Basic Producer/Consumer in Java**

---

### ✅ Step 1: Kafka Installation (Local)

You can either install Kafka on your local system or use Docker.

#### 🐧 **Option A: Install Locally (Linux/macOS)**

1. Download Kafka:  
    [https://kafka.apache.org/downloads](https://kafka.apache.org/downloads)
    
2. Extract and navigate:
    
    ```bash
    tar -xzf kafka_2.13-3.6.1.tgz
    cd kafka_2.13-3.6.1
    ```
    
3. Start **Zookeeper** and **Kafka broker**:
    
    ```bash
    bin/zookeeper-server-start.sh config/zookeeper.properties
    bin/kafka-server-start.sh config/server.properties
    ```
    

---

#### 🐳 **Option B: Run via Docker**

```yaml
# docker-compose.yml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:latest
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

Run:

```bash
docker-compose up -d
```

---

### ✅ Step 2: Create Spring Boot Project

Create a new Spring Boot project (via Spring Initializr):

**Dependencies:**

- Spring Web
    
- Spring for Apache Kafka
    
- Lombok (optional)
    

---

### ✅ Step 3: Maven Configuration

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
    <!-- Other Spring Boot dependencies -->
</dependencies>
```

---

### ✅ Step 4: application.yml or application.properties

```yaml
# application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: my-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

---

### ✅ Step 5: Kafka Producer Code

```java
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send("my-topic", message);
    }
}
```

---

### ✅ Step 6: Kafka Consumer Code

```java
@Service
public class KafkaConsumer {

    @KafkaListener(topics = "my-topic", groupId = "my-group")
    public void listen(String message) {
        System.out.println("Received: " + message);
    }
}
```

---

### ✅ Step 7: Expose an API to Send Messages

```java
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducer producer;

    public KafkaController(KafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestParam String message) {
        producer.sendMessage(message);
        return ResponseEntity.ok("Message sent to Kafka");
    }
}
```

---

🧪 Test:

```bash
curl -X POST "http://localhost:8080/kafka/send?message=HelloKafka"
```

Output (in logs):

```
Received: HelloKafka
```

---

✅ That completes a basic **Spring Boot Kafka Producer and Consumer** setup!


Would you like to continue to **Part 3: Kafka with JSON messages and Custom DTOs**?

Great! Let’s continue.

---

## 🔹 **Part 3: Kafka with JSON Messages and Custom DTOs**

In real-world applications, we rarely send plain strings. Instead, we send **structured objects** (like `User`, `Order`, `Event`) as **JSON**.

---

### ✅ Step 1: Define a DTO (POJO)

```java
public class User {
    private String id;
    private String name;
    private String email;

    // Constructors, Getters, Setters
}
```

(Optional: Use Lombok)

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;
    private String name;
    private String email;
}
```

---

### ✅ Step 2: Configure Kafka to Use JSON Serializer

Update `application.yml`:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
```

---

### ✅ Step 3: Send JSON from Producer

```java
@Service
public class KafkaJsonProducer {

    private final KafkaTemplate<String, User> kafkaTemplate;

    public KafkaJsonProducer(KafkaTemplate<String, User> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUser(User user) {
        kafkaTemplate.send("json-topic", user);
    }
}
```

---

### ✅ Step 4: Listen to JSON in Consumer

```java
@Service
public class KafkaJsonConsumer {

    @KafkaListener(topics = "json-topic", groupId = "json-group", containerFactory = "userKafkaListenerFactory")
    public void consume(User user) {
        System.out.println("Received JSON User: " + user);
    }
}
```

---

### ✅ Step 5: Configure Listener Container Factory

You need to define a custom `ConcurrentKafkaListenerContainerFactory` to tell Kafka how to deserialize JSON.

```java
@Configuration
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, User> userConsumerFactory() {
        JsonDeserializer<User> deserializer = new JsonDeserializer<>(User.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                        ConsumerConfig.GROUP_ID_CONFIG, "json-group",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer
                ),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, User> userKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, User> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userConsumerFactory());
        return factory;
    }
}
```

---

### ✅ Step 6: Expose API to Send JSON

```java
@RestController
@RequestMapping("/json-kafka")
public class JsonKafkaController {

    private final KafkaJsonProducer producer;

    public JsonKafkaController(KafkaJsonProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendUser(@RequestBody User user) {
        producer.sendUser(user);
        return ResponseEntity.ok("User sent to Kafka");
    }
}
```

---

🧪 **Test via Postman or curl:**

```bash
curl -X POST http://localhost:8080/json-kafka/send \
     -H "Content-Type: application/json" \
     -d '{"id":"u1","name":"Vijay","email":"vijay@example.com"}'
```

🧾 Output in logs:

```
Received JSON User: User(id=u1, name=Vijay, email=vijay@example.com)
```

---

✅ That completes sending **JSON messages** with Kafka using custom DTOs.

Would you like to proceed to **Part 4: Kafka Message Acknowledgment, Error Handling, and Retry**?
Awesome! Let’s dive into:

---

## 🔹 **Part 4: Kafka Acknowledgment, Error Handling & Retry in Spring Boot**

---

### ✅ 1. **Kafka Acknowledgment Modes**

Kafka supports **manual and automatic message acknowledgments**. In Spring Kafka, the default is **automatic**, but you can switch to **manual** when you need better control.

---

#### 🔹 Acknowledgment Modes in Spring

|Mode|Description|
|---|---|
|`RECORD`|Commit after each record|
|`BATCH`|Commit after a batch of records|
|`TIME`|Commit after a time interval|
|`MANUAL`|You commit manually using `Acknowledgment.acknowledge()`|
|`MANUAL_IMMEDIATE`|Manual, commits immediately|

---

### ✅ 2. Manual Acknowledgment Example

Use this if you want to commit only after successful processing:

```java
@KafkaListener(
    topics = "json-topic",
    groupId = "manual-group",
    containerFactory = "manualAckListenerFactory"
)
public void consumeWithAck(User user, Acknowledgment ack) {
    try {
        System.out.println("Processing user: " + user);
        // Business logic
        ack.acknowledge(); // Commit offset
    } catch (Exception e) {
        System.err.println("Error processing: " + e.getMessage());
    }
}
```

---

### ✅ 3. Configure Manual Acknowledgment Listener Factory

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, User> manualAckListenerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, User> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(userConsumerFactory());
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    return factory;
}
```

---

### ✅ 4. Error Handling in Kafka Consumers

You can configure what happens when a message fails (e.g. due to deserialization or business logic error).

#### 🔸 Basic Error Handler Example

```java
factory.setCommonErrorHandler(new DefaultErrorHandler((consumerRecord, exception) -> {
    System.err.println("Error occurred: " + exception.getMessage());
}));
```

---

### ✅ 5. Retry with Backoff Policy

You can add **retry logic** using `DefaultErrorHandler` and `FixedBackOff` or `ExponentialBackOff`.

```java
@Bean
public DefaultErrorHandler errorHandler() {
    FixedBackOff fixedBackOff = new FixedBackOff(3000L, 2); // 3 sec delay, 2 retries

    DefaultErrorHandler handler = new DefaultErrorHandler((record, ex) -> {
        System.err.println("Handling failed record: " + record);
        ex.printStackTrace();
    }, fixedBackOff);

    return handler;
}

@Bean
public ConcurrentKafkaListenerContainerFactory<String, User> retryKafkaListenerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, User> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(userConsumerFactory());
    factory.setCommonErrorHandler(errorHandler());
    return factory;
}
```

Then update your listener to use this factory:

```java
@KafkaListener(
    topics = "json-topic",
    groupId = "retry-group",
    containerFactory = "retryKafkaListenerFactory"
)
public void retryableListener(User user) {
    if (user.getEmail().contains("fail")) {
        throw new RuntimeException("Simulated failure");
    }
    System.out.println("Successfully processed: " + user);
}
```

---

### ✅ 6. Dead Letter Topic (DLT)

When all retries fail, you can send the message to a **Dead Letter Topic**.

You can set this in `DefaultErrorHandler`:

```java
handler.setDeadLetterPublishingRecoverer(new DeadLetterPublishingRecoverer(kafkaTemplate));
```

You’ll need to create a separate consumer for the dead letter topic to inspect or retry messages later.

---

✅ That covers **acknowledgment, retry, and error handling** in Kafka with Spring Boot!

Would you like to continue to **Part 5: Kafka Streams or Kafka Transactions** next?
Excellent! Let’s move on to:

---

## 🔹 **Part 5: Kafka Streams with Spring Boot (Real-Time Processing)**

Kafka Streams is a **powerful library** built on top of Kafka to **process data in real time**—no need for an external stream processing engine like Spark or Flink.

---

### ✅ Use Cases of Kafka Streams

- Aggregation (sum, count, average)
    
- Windowed operations (e.g., count per 1-minute window)
    
- Joins between topics
    
- Real-time transformations
    

---

### ✅ 1. Add Kafka Streams Dependency

Add to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-streams</artifactId>
</dependency>
```

---

### ✅ 2. Enable Kafka Streams in `application.yml`

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    streams:
      application-id: stream-app
```

---

### ✅ 3. Simple Kafka Streams Processor

```java
@Configuration
public class StreamProcessor {

    @Bean
    public KStream<String, String> processStream(StreamsBuilder builder) {
        KStream<String, String> stream = builder.stream("input-topic");

        stream
            .mapValues(value -> value.toUpperCase())
            .to("output-topic");

        return stream;
    }
}
```

**What this does:**

- Reads from `input-topic`
    
- Converts all messages to uppercase
    
- Writes to `output-topic`
    

---

### ✅ 4. Test with Producer and Consumer

**Send a message to `input-topic`**:

```java
// Reuse KafkaProducer class from earlier
kafkaTemplate.send("input-topic", "hello kafka streams");
```

**Create a consumer for `output-topic`:**

```java
@KafkaListener(topics = "output-topic", groupId = "stream-group")
public void consume(String message) {
    System.out.println("Received from output-topic: " + message);
}
```

**Output:**

```
Received from output-topic: HELLO KAFKA STREAMS
```

---

### ✅ 5. Stateful Processing – Counting Word Occurrences

```java
@Bean
public KStream<String, String> wordCountStream(StreamsBuilder builder) {
    KStream<String, String> stream = builder.stream("text-input");

    stream.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
          .groupBy((key, word) -> word)
          .count(Materialized.as("word-counts"))
          .toStream()
          .to("word-count-output", Produced.with(Serdes.String(), Serdes.Long()));

    return stream;
}
```

**This stream:**

- Splits sentences into words
    
- Groups by word
    
- Counts occurrences
    
- Writes to `word-count-output` topic
    

---

### ✅ 6. Kafka Streams vs Consumers

|Feature|Kafka Consumer|Kafka Streams|
|---|---|---|
|Message consumption|Manual|Declarative|
|Stateful operations|Manual tracking|Built-in state stores|
|Aggregation, join, window|Complex|Simple|
|Use case|Event listeners|Stream processing|

---

✅ That’s a solid intro to Kafka Streams in Spring Boot!

Would you like to proceed to **Part 6: Kafka Transactions & Exactly-Once Semantics**?
Great! Let’s continue to:

---

## 🔹 **Part 6: Kafka Transactions & Exactly-Once Semantics (EOS)**

In high-integrity systems (like **banking, order processing**), you must ensure that messages are:

- **Produced exactly once**
    
- **Consumed and processed exactly once**
    
- No **duplicate events**, even if retry or failure happens
    

Kafka supports this via **transactions**.

---

### ✅ Why Kafka Transactions?

Without transactions:

- You might produce a message **after** processing, and if the service crashes, it could be **lost** or **duplicated**.
    
- Consumers might process the same message **twice** (e.g., on failure + retry).
    

With transactions:

- Kafka guarantees **atomic writes across topics**
    
- Ensures **exactly-once delivery**
    

---

### ✅ 1. Enable Transactions in Producer Configuration

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      transaction-id-prefix: my-tx-
```

This enables **transactional producer** in Spring Boot.

---

### ✅ 2. Enable Transactions in Java Config (if needed)

```java
@Configuration
public class KafkaTxConfig {

    @Bean
    public KafkaTransactionManager<String, String> kafkaTransactionManager(ProducerFactory<String, String> pf) {
        return new KafkaTransactionManager<>(pf);
    }
}
```

---

### ✅ 3. Use KafkaTemplate in a Transaction

```java
@Service
public class TransactionalKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TransactionalKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional("kafkaTransactionManager")
    public void sendInTransaction(String data) {
        kafkaTemplate.executeInTransaction(kt -> {
            kt.send("topic-1", data);
            kt.send("topic-2", "Copy: " + data);
            return true;
        });

        // If any exception occurs, both messages will be rolled back
    }
}
```

---

### ✅ 4. Simulate Failure to Test Atomicity

```java
@Transactional("kafkaTransactionManager")
public void sendWithError(String data) {
    kafkaTemplate.executeInTransaction(kt -> {
        kt.send("topic-1", data);
        if (data.contains("fail")) {
            throw new RuntimeException("Simulated failure");
        }
        kt.send("topic-2", "Copy: " + data);
        return true;
    });
}
```

📌 If `"fail"` is present, **neither message is committed**. You avoid half-completed state.

---

### ✅ 5. Ensure Consumer Idempotency

You should use **Kafka’s idempotent producer** and **idempotent consumer logic**.

In `application.yml`, this is automatically set with Spring Boot when using transactions, but if needed:

```yaml
spring.kafka.producer.properties.enable.idempotence: true
```

Consumer side:

- Use **Kafka-aware storage** (e.g., process offset within a DB transaction)
    
- Avoid reprocessing by checking **message keys or metadata**
    

---

### 🧠 Kafka Transactions Support:

|Operation|Supported|
|---|---|
|Multiple topic writes|✅|
|Sending + consuming in 1 transaction|✅ (using Kafka Streams)|
|KafkaTemplate transaction|✅|
|Exactly-once to external DB|❌ (use 2PC or outbox pattern)|

---

✅ That covers **Kafka Transactions and Exactly-Once Semantics**.

Would you like to continue to:

- **Part 7: Kafka Schema Registry & Avro Serialization**
    
- or
    
- **Part 7: Kafka Security (SSL, ACLs, SASL)**?

Excellent choice! Let's move on to:

---

## 🔹 **Part 7A: Kafka Schema Registry & Avro Serialization**

---

### ✅ Why Use Schema Registry & Avro?

#### Problem:

- Using JSON is flexible, but **lacks schema validation**.
    
- No enforced versioning or compatibility check.
    
- Payloads can become **large and error-prone**.
    

#### Solution:

Use:

- **Apache Avro** for compact, binary, schema-based serialization.
    
- **Confluent Schema Registry** to store and manage versioned schemas centrally.
    

---

### ✅ Overview

|Component|Role|
|---|---|
|**Avro**|Serialization format|
|**Schema Registry**|Central server that stores message schemas|
|**Kafka Producer**|Serializes Java objects to Avro|
|**Kafka Consumer**|Deserializes Avro back into Java objects|

---

### ✅ Step 1: Add Dependencies

```xml
<!-- Avro & Schema Registry support -->
<dependency>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-avro-serializer</artifactId>
    <version>7.5.0</version> <!-- use compatible version -->
</dependency>
<dependency>
    <groupId>org.apache.avro</groupId>
    <artifactId>avro</artifactId>
</dependency>
```

> ⚠️ You also need to add the Confluent repository if not using Confluent Platform:

```xml
<repository>
    <id>confluent</id>
    <url>https://packages.confluent.io/maven/</url>
</repository>
```

---

### ✅ Step 2: Define an Avro Schema

Save it as `user.avsc`:

```json
{
  "namespace": "com.example.avro",
  "type": "record",
  "name": "User",
  "fields": [
    {"name": "id", "type": "string"},
    {"name": "name", "type": "string"},
    {"name": "email", "type": "string"}
  ]
}
```

---

### ✅ Step 3: Generate Java Class from Avro

Use the Avro Maven Plugin:

```xml
<plugin>
  <groupId>org.apache.avro</groupId>
  <artifactId>avro-maven-plugin</artifactId>
  <version>1.11.1</version>
  <executions>
    <execution>
      <goals><goal>schema</goal></goals>
      <configuration>
        <sourceDirectory>${project.basedir}/src/main/avro</sourceDirectory>
        <outputDirectory>${project.basedir}/src/main/java</outputDirectory>
      </configuration>
    </execution>
  </executions>
</plugin>
```

Then place `user.avsc` inside `src/main/avro`, and run:

```bash
mvn generate-sources
```

---

### ✅ Step 4: Update application.yml for Avro + Schema Registry

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    properties:
      schema.registry.url: http://localhost:8081
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
      properties:
        specific.avro.reader: true
```

---

### ✅ Step 5: Producer with Avro

```java
@Service
public class AvroProducer {

    private final KafkaTemplate<String, User> kafkaTemplate;

    public AvroProducer(KafkaTemplate<String, User> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(User user) {
        kafkaTemplate.send("avro-topic", user);
    }
}
```

---

### ✅ Step 6: Consumer with Avro

```java
@KafkaListener(topics = "avro-topic", groupId = "avro-group")
public void consume(User user) {
    System.out.println("Received Avro user: " + user);
}
```

---

### ✅ Step 7: Start Schema Registry

You need Confluent Platform or Docker.

#### 🐳 Docker Compose example:

```yaml
version: '2'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  schema-registry:
    image: confluentinc/cp-schema-registry:7.5.0
    ports:
      - "8081:8081"
    environment:
      SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS: PLAINTEXT://kafka:9092
```

Then run:

```bash
docker-compose up -d
```

✅ Schema Registry now runs at:  
**[http://localhost:8081](http://localhost:8081/)**

---

### ✅ Example Output in Schema Registry

Once a message is sent, you can GET:

```bash
curl http://localhost:8081/subjects
```

You’ll see:

```json
["avro-topic-value"]
```

---

✅ That wraps up using **Kafka with Schema Registry and Avro serialization**.

Would you like to continue to **Part 7B: Kafka Security (SSL, ACL, SASL)** next?
Great! Let's dive into:

---

## 🔹 **Part 7B: Kafka Security — SSL, ACLs, and SASL**

Kafka supports enterprise-grade security using:

|Layer|Purpose|
|---|---|
|**SSL**|Encrypt data in transit|
|**SASL**|Authenticate clients|
|**ACLs**|Authorize access to Kafka topics|

We’ll cover all three: **encryption, authentication, and authorization**.

---

## ✅ 1. SSL Encryption in Kafka

Kafka uses TLS/SSL to encrypt data between:

- Producers & Brokers
    
- Consumers & Brokers
    
- Brokers & Brokers
    

---

### 🔹 Step 1: Generate Keystore & Truststore

Use Java's `keytool`:

```bash
# Generate a Kafka broker keystore
keytool -genkey -alias kafka-broker -keyalg RSA -keystore kafka.server.keystore.jks \
  -validity 365 -storepass password -keypass password \
  -dname "CN=localhost"

# Export the certificate
keytool -export -alias kafka-broker -file kafka.cert -keystore kafka.server.keystore.jks -storepass password

# Create a truststore and import the certificate
keytool -import -alias kafka-broker -file kafka.cert -keystore kafka.server.truststore.jks -storepass password -noprompt
```

Repeat similar steps for **client keystore and truststore**, or use the broker’s cert for local development.

---

### 🔹 Step 2: Configure Kafka Broker with SSL

In `server.properties`:

```properties
listeners=SSL://:9093
security.inter.broker.protocol=SSL

ssl.keystore.location=/path/kafka.server.keystore.jks
ssl.keystore.password=password
ssl.key.password=password

ssl.truststore.location=/path/kafka.server.truststore.jks
ssl.truststore.password=password
```

---

### 🔹 Step 3: Configure Spring Boot Kafka Client (Producer/Consumer)

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9093
    security.protocol: SSL
    ssl:
      trust-store-location: classpath:kafka.client.truststore.jks
      trust-store-password: password
      key-store-location: classpath:kafka.client.keystore.jks
      key-store-password: password
      key-password: password
```

---

## ✅ 2. SASL Authentication (Optional)

Kafka supports SASL mechanisms like:

- PLAIN (username/password)
    
- SCRAM (better security)
    
- GSSAPI (Kerberos)
    

### 🔹 SASL PLAIN Example

**Broker Config:**

```properties
listeners=SASL_PLAINTEXT://:9094
security.inter.broker.protocol=SASL_PLAINTEXT
sasl.mechanism.inter.broker.protocol=PLAIN
sasl.enabled.mechanisms=PLAIN
```

**JAAS file (kafka_server_jaas.conf):**

```ini
KafkaServer {
   org.apache.kafka.common.security.plain.PlainLoginModule required
   username="admin"
   password="admin-secret"
   user.admin="admin-secret"
   user.user1="user1-secret";
};
```

Start Kafka with:

```bash
KAFKA_OPTS="-Djava.security.auth.login.config=/path/kafka_server_jaas.conf" ./kafka-server-start.sh config/server.properties
```

**Client (Spring) Config:**

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9094
    properties:
      security.protocol: SASL_PLAINTEXT
      sasl.mechanism: PLAIN
      sasl.jaas.config: |
        org.apache.kafka.common.security.plain.PlainLoginModule required
        username="user1"
        password="user1-secret";
```

---

## ✅ 3. Kafka Authorization using ACLs

ACLs control **who can access which topics**.

Enable in `server.properties`:

```properties
authorizer.class.name=kafka.security.authorizer.AclAuthorizer
allow.everyone.if.no.acl.found=false
super.users=User:admin
```

### 🔹 Create ACLs

```bash
# Allow user1 to produce to topic 'orders'
kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 \
  --add --allow-principal User:user1 --operation Write --topic orders

# Allow user1 to consume from 'orders'
kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 \
  --add --allow-principal User:user1 --operation Read --topic orders
```

---

## 🔐 Summary of Kafka Security

|Security Feature|Tool/Config|
|---|---|
|Encryption|SSL/TLS (Keystore, Truststore)|
|Authentication|SASL (PLAIN, SCRAM, GSSAPI)|
|Authorization|ACLs via `kafka-acls.sh`|
|Spring Boot Support|Native via `spring.kafka.*` configs|

---

✅ That completes **Kafka Security with SSL, SASL, and ACLs**.

Would you like to continue to:

- **Part 8: Kafka Monitoring & Tooling**
    
- or
    
- **Part 9: Kafka Best Practices for Production**?


---

## 🔹 **Part 8: Kafka Monitoring & Tooling**

Monitoring is essential in Kafka because it’s a **distributed system** that handles high-throughput data pipelines. Without proper observability, diagnosing lag, broker failure, or message loss becomes extremely difficult.

---

### ✅ What to Monitor in Kafka?

|Metric Area|What to Watch|
|---|---|
|**Broker Health**|Disk usage, memory, CPU, heap|
|**Topic/Partition**|Lag, ISR (in-sync replicas), under-replicated partitions|
|**Producer**|Send rate, retries, exceptions|
|**Consumer**|Lag, commit frequency|
|**ZooKeeper**|Session count, latency, outages|
|**JVM**|GC, thread count, heap usage|

---

## 🛠️ Tooling Overview

|Tool|Purpose|
|---|---|
|**JMX**|Native metric source (Java Management Extensions)|
|**Prometheus + Grafana**|Open-source metrics and dashboards|
|**Kafka Manager / UI**|Topic management and visualization|
|**Burrow**|Consumer lag monitoring|
|**Conduktor**|Developer-friendly Kafka desktop GUI|
|**Kafdrop**|Lightweight Kafka web UI (topics, consumers, messages)|

---

### ✅ Option 1: Use JMX Metrics (Built-in)

Kafka exposes a rich set of metrics via **JMX**.

Example:

```bash
JMX_PORT=9999 KAFKA_HEAP_OPTS="-Xmx512M" ./kafka-server-start.sh config/server.properties
```

You can then connect with:

- **JConsole**
    
- **VisualVM**
    
- **Prometheus JMX Exporter**
    

---

### ✅ Option 2: Prometheus + Grafana Monitoring

#### 🔹 Step 1: Add JMX Exporter Agent

Download the JMX exporter jar from:  
[https://github.com/prometheus/jmx_exporter](https://github.com/prometheus/jmx_exporter)

Run Kafka like this:

```bash
KAFKA_OPTS="-javaagent:/path/jmx_prometheus_javaagent-0.19.0.jar=7071:/path/kafka-2_0_0.yml"
```

Sample `kafka-2_0_0.yml` config:

```yaml
startDelaySeconds: 0
jmxUrl: ""
lowercaseOutputName: true
lowercaseOutputLabelNames: true
rules:
  - pattern: "kafka.server<type=(.+), name=(.+)><>Value"
    name: kafka_$1_$2
    type: GAUGE
    labels:
      instance: "kafka-broker"
```

#### 🔹 Step 2: Prometheus Config

```yaml
scrape_configs:
  - job_name: 'kafka'
    static_configs:
      - targets: ['localhost:7071']
```

#### 🔹 Step 3: Grafana Dashboard

Use ready-made dashboards like:

- [Kafka Overview Dashboard (ID: 7589)](https://grafana.com/grafana/dashboards/7589/)
    
- [Kafka Exporter Dashboard (ID: 721)](https://grafana.com/grafana/dashboards/721/)
    

---

### ✅ Option 3: Kafka UI Tools

|Tool|Features|
|---|---|
|**Kafdrop**|View topics, messages, consumer groups. Lightweight web UI.|
|**Conduktor**|All-in-one GUI for Kafka devs. Free and paid.|
|**AKHQ**|Open-source Kafka manager (UI, metrics, ACLs, schema registry).|
|**Burrow**|Monitor consumer group lags and alerting.|

#### 🐳 Run Kafdrop via Docker:

```yaml
version: '3'
services:
  kafdrop:
    image: obsidiandynamics/kafdrop
    ports:
      - "9000:9000"
    environment:
      KAFKA_BROKERCONNECT: kafka:9092
```

Access via: [http://localhost:9000](http://localhost:9000/)

---

### ✅ Best Practice: Set Alert Rules

Set alerts for:

- Broker down
    
- Under-replicated partitions
    
- Consumer group lag > threshold
    
- GC time > 70%
    
- Disk usage > 85%
    

---

✅ That completes **Kafka Monitoring & Tooling**.

Would you like to continue to:

- ✅ **Part 9: Kafka in Production – Best Practices**

Great! Let’s dive into:

---

## 🔹 **Part 9: Kafka in Production – Best Practices**

Running Apache Kafka in production requires thoughtful planning to ensure **reliability, scalability, and observability**. This part covers key strategies and configurations used by seasoned engineers.

---

### ✅ 1. Topic Design

#### 🔹 Partitioning

- Use **multiple partitions** for scalability and parallelism.
    
- Partition count cannot be changed **easily** later; plan upfront.
    
- Use a **partition key** that distributes messages **evenly**.
    

> **Tip**: Avoid using customer ID or order ID as key if they skew traffic.

#### 🔹 Replication

- Default replication factor is 1 (not fault tolerant).
    
- Use **replication.factor = 3** in production for HA.
    

---

### ✅ 2. Retention Strategy

Kafka is a **log-based system**—you must define:

- How long data stays
    
- How big each topic can grow
    

```properties
# Keep data for 7 days
log.retention.hours=168

# Or retain last 5 GB per partition
log.retention.bytes=5368709120
```

> You can override this **per topic** via `kafka-topics.sh` or AdminClient.

---

### ✅ 3. Producer Best Practices

|Config|Description|
|---|---|
|`acks=all`|Ensures broker writes to all in-sync replicas|
|`retries=10`|Retry on transient failures|
|`enable.idempotence=true`|Guarantees **exactly-once** in producer|
|`batch.size`, `linger.ms`|Tune for throughput|
|`compression.type=snappy`|Reduce bandwidth and storage|

#### 🧠 Key Insight:

Enable **idempotence + acks=all** for reliable delivery.

---

### ✅ 4. Consumer Best Practices

|Config|Description|
|---|---|
|`enable.auto.commit=false`|Manual commit gives you control|
|`max.poll.interval.ms`|Increase if long processing|
|`max.poll.records`|Tune for batch size|
|`isolation.level=read_committed`|Use with transactional producer|

#### 🧠 Tip:

Commit offsets **only after** successful processing + DB transaction (to avoid duplicates).

---

### ✅ 5. Broker Configuration

|Config|Recommendation|
|---|---|
|`num.partitions=6`|Default partition count (can override per topic)|
|`log.retention.hours=168`|Retention tuning|
|`message.max.bytes=10485760`|Max message size|
|`num.replica.fetchers=4`|Faster replication|
|`auto.create.topics.enable=false`|Prevent accidental topic creation|

---

### ✅ 6. JVM & Disk Considerations

- Kafka is JVM-based → Monitor **GC, heap, thread count**
    
- Use **XFS or ext4** file system with SSDs
    
- Keep **1 disk per broker** for I/O performance
    
- Use **RAID-10** or **multiple mounts** for HA
    

---

### ✅ 7. Scaling Kafka

|Scale|Strategy|
|---|---|
|**Write throughput**|Add partitions to topics|
|**Read throughput**|Add more consumers (in same group)|
|**Storage**|Add more brokers or offload to S3 (with Tiered Storage in Confluent)|

---

### ✅ 8. Deployment Tips

|Area|Best Practice|
|---|---|
|Zookeeper|Use **odd number** of nodes (e.g., 3, 5)|
|Broker ID|Unique per broker (`broker.id`)|
|DNS|Use `advertised.listeners` for external clients|
|Monitoring|Use Prometheus + Grafana + Alertmanager|
|Rolling Updates|Always use **graceful stop** (`controlled.shutdown.enable=true`)|

---

### ✅ 9. Data Governance

- Use **Schema Registry** to enforce Avro schema compatibility
    
- Maintain **schema evolution rules** (backward, forward compatibility)
    
- Use **topic naming conventions**:
    
    ```
    project.environment.entity.version
    e.g. orders.prod.payment.v1
    ```
    

---

### ✅ 10. Backup and Disaster Recovery

- Use Kafka **MirrorMaker 2** to mirror data to DR cluster
    
- Backup **topic configs**, **schema registry**, and **offsets**
    
- Keep a **cold standby** Kafka cluster (or cloud managed cluster)
    

---

## 📦 TL;DR: Kafka Production Checklist

-  Topics with enough partitions & replication
    
-  Monitoring in place (Prometheus, Grafana, Kafdrop)
    
-  Acknowledgment + idempotent producer
    
-  Manual offset commit after success
    
-  Schema Registry for type safety
    
-  Security: SSL + ACLs + SASL
    
-  Disk + JVM tuned
    
-  Backup strategy
    

---

✅ This concludes **Kafka in Production – Best Practices**.

## 🧠 **Kafka + Spring Boot: Final Recap**

---

### ✅ **Part 1: Kafka Fundamentals**

- Kafka is a **distributed event streaming platform**.
    
- Core concepts:
    
    - **Topic**, **Partition**, **Offset**
        
    - **Producer**, **Consumer**, **Broker**, **Zookeeper**
        
- Kafka is high-throughput, durable, and horizontally scalable.
    

---

### ✅ **Part 2: Basic Producer & Consumer**

- Set up Kafka in Spring Boot using `KafkaTemplate` and `@KafkaListener`.
    
- Defined `application.yml` with:
    
    - `bootstrap-servers`
        
    - serializers/deserializers
        
- Created simple REST APIs to send messages.
    

---

### ✅ **Part 3: JSON Messaging**

- Replaced String messages with structured Java DTOs (`User`, `Order`).
    
- Used:
    
    - `JsonSerializer`, `JsonDeserializer`
        
    - `@KafkaListener` with custom container factory
        
- Exchanged JSON between services cleanly and safely.
    

---

### ✅ **Part 4: Acknowledgment, Retry & Error Handling**

- Configured different **AckModes** (`MANUAL`, `RECORD`, etc.).
    
- Used `Acknowledgment.acknowledge()` to commit offset **after success**.
    
- Implemented **retry** with `DefaultErrorHandler` + `FixedBackOff`.
    
- Sent failures to **Dead Letter Topic (DLT)**.
    

---

### ✅ **Part 5: Kafka Streams**

- Used `KStream` to perform:
    
    - Real-time mapping, filtering
        
    - Aggregations (like word count)
        
- Used `StreamsBuilder`, `@Bean`, and declarative logic for processing.
    
- Compared Streams vs Consumer approach.
    

---

### ✅ **Part 6: Kafka Transactions & Exactly-Once**

- Enabled transactions in producer using:
    
    ```yaml
    spring.kafka.producer.transaction-id-prefix
    ```
    
- Used `KafkaTemplate.executeInTransaction()` to send atomic messages.
    
- Ensured **exactly-once** delivery using:
    
    - `acks=all`, `enable.idempotence=true`, transactional commits
        

---

### ✅ **Part 7A: Schema Registry & Avro**

- Used **Apache Avro** for binary, schema-based messaging.
    
- Integrated **Confluent Schema Registry**.
    
- Benefits:
    
    - Enforced schema validation
        
    - Compact messages
        
    - Schema evolution support
        
- Generated Avro classes with Maven plugin.
    

---

### ✅ **Part 7B: Kafka Security**

- Secured Kafka using:
    
    - **SSL/TLS** for encryption
        
    - **SASL (PLAIN, SCRAM)** for authentication
        
    - **ACLs** for authorization
        
- Configured Spring Boot to connect with secured Kafka clusters.
    

---

### ✅ **Part 8: Kafka Monitoring & Tooling**

- Monitored brokers, topics, lag, GC using:
    
    - **Prometheus + Grafana**
        
    - **Kafka JMX exporter**
        
    - **Kafdrop**, **Conduktor**, **AKHQ**
        
- Set up alerting on consumer lag, disk, memory, GC, etc.
    

---

### ✅ **Part 9: Kafka Best Practices for Production**

- Designed scalable and durable topics:
    
    - Proper **partitioning**
        
    - **Replication**
        
    - Thoughtful **retention**
        
- Tuning tips:
    
    - Batch size, linger.ms, acks
        
    - JVM, disk, GC
        
- Deployment:
    
    - Zookeeper HA, graceful shutdowns
        
    - Versioning, schema compatibility
        
    - MirrorMaker2 for DR
        

---

## 🧭 Next Learning Paths (Optional)

|Area|Goal|
|---|---|
|**Kafka Connect**|Integrate Kafka with databases (CDC) using **Debezium**|
|**Kafka + Spring Cloud Stream**|Use `@StreamListener` & functional bindings|
|**Kafka on Kubernetes**|Deploy Kafka on K8s using Helm, Strimzi|
|**Kafka with S3 or Tiered Storage**|Store Kafka logs on S3 (Confluent/Redpanda)|
|**Event-driven architecture**|Design patterns, choreography vs orchestration|

---

## 🎓 Your Kafka Skill Level Now

You're now well-versed in:

✅ Kafka Core Concepts  
✅ Spring Boot Integration  
✅ JSON & Avro Messaging  
✅ Streams, Transactions, Security  
✅ Monitoring, Best Practices

You’re production-ready 👏

---

Would you like a downloadable **cheatsheet**, a **Kafka interview Q&A guide**, or to start learning **Debezium + Kafka Connect** next?