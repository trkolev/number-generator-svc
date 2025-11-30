# Number Generator Application

A Spring Boot microservice for generating unique, sequential claim and policy numbers with thread-safe atomic operations and persistent storage.

## Tech Stack

### Core Framework
- **Spring Boot 3.5.7** - Main application framework
- **Java 17** - Programming language
- **Maven** - Build and dependency management

### Key Dependencies
- **Spring Boot Starter Web** - RESTful API endpoints and web server
- **Spring Boot Starter Data Redis** - Redis integration for data persistence and atomic operations
- **Spring Boot Starter Validation** - Input validation capabilities
- **Lombok** - Reduces boilerplate code with annotations
- **Testcontainers** (v1.19.8) - Integration testing with containerized dependencies

### Data Storage
- **Redis** - In-memory data store used for:
  - Atomic counter operations (thread-safe sequence generation)
  - Persistent storage of generated number metadata
  - Connection pooling via Lettuce client

## Features

### 1. Claim Number Generation
- **Endpoint**: `GET /api/v1/claim-numbers`
- **Format**: `Cl-0000000001` (prefix "Cl-" followed by 10-digit zero-padded number)
- **Functionality**: Generates sequential, unique claim numbers
- **Thread-Safe**: Uses Redis atomic operations to ensure uniqueness across concurrent requests

### 2. Policy Number Generation
- **Endpoint**: `GET /api/v1/policy-numbers`
- **Format**: `SG/08/0000000001` (prefix "SG/08/" followed by 10-digit zero-padded number)
- **Functionality**: Generates sequential, unique policy numbers
- **Thread-Safe**: Uses Redis atomic operations to ensure uniqueness across concurrent requests

### 3. Atomic Sequence Management
- Utilizes `RedisAtomicLong` for thread-safe counter increments
- Separate counters for claim and policy numbers
- Guarantees no duplicate numbers even under high concurrency

### 4. Data Persistence
- Stores generated number metadata in Redis using Spring Data Redis
- Separate hash storage for claim and policy number generators
- Enables tracking and auditing of generated numbers

## API Endpoints

### Generate Claim Number
```http
GET /api/v1/claim-numbers
```

**Response:**
```json
"Cl-0000000001"
```

### Generate Policy Number
```http
GET /api/v1/policy-numbers
```

**Response:**
```json
"SG/08/0000000001"
```

## Configuration

### Application Properties
The application is configured via `application.properties`:

```properties
spring.application.name=number-generator
server.port=8081

# Redis Connection
spring.data.redis.host=192.168.0.13
spring.data.redis.port=6379

# Connection Pool Tuning
spring.data.redis.lettuce.pool.max-active=8
spring.data.redis.lettuce.pool.max-idle=8
spring.data.redis.lettuce.pool.min-idle=0
spring.data.redis.lettuce.pool.time-between-eviction-runs=10s
```

### Redis Configuration
- **Host**: Configurable via `spring.data.redis.host`
- **Port**: Configurable via `spring.data.redis.port`
- **Connection Pool**: Lettuce connection pool with configurable settings

## Integrations

### External Systems Integration

#### 1. Redis Integration
- **Purpose**: Atomic counter management and data persistence
- **Connection**: TCP connection to Redis server
- **Usage**:
  - Atomic sequence generation using `RedisAtomicLong`
  - Persistent storage of number generator entities
  - Hash-based storage for claim and policy number metadata

#### 2. REST API Integration
The application exposes REST endpoints that can be consumed by:
- **Insurance Management Systems** - For generating claim and policy numbers
- **Microservices** - As a centralized number generation service
- **Third-party Applications** - Via HTTP/HTTPS requests

**Integration Pattern**: Synchronous HTTP GET requests

### Internal Architecture

#### Service Layer
- `ClaimNumberGeneratorService` - Business logic for claim number generation
- `PolicyNumberGeneratorService` - Business logic for policy number generation

#### Repository Layer
- `ClaimNumberGeneratorRepository` - Data access for claim numbers
- `PolicyNumberGeneratorRepository` - Data access for policy numbers

#### Model Layer
- `ClaimNumberGenerator` - Entity model for claim number metadata
- `PolicyNumberGenerator` - Entity model for policy number metadata

## Testing

The application includes comprehensive test coverage:

- **Unit Tests**: Model and service layer testing
- **Integration Tests**: Full end-to-end testing with Testcontainers
- **Controller Tests**: REST endpoint validation

### Running Tests
```bash
mvn test
```

### Integration Test Requirements
Integration tests use Testcontainers and require Docker to be available. Set the system property:
```bash
-Ddocker.available=true
```

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Redis server (accessible at configured host/port)

### Build
```bash
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

Or using the generated JAR:
```bash
java -jar target/number-generator-0.0.1-SNAPSHOT.jar
```

### Docker Support
The application can be containerized and deployed. Ensure Redis is accessible from the container environment.

## Architecture Highlights

- **Thread-Safe Design**: Uses Redis atomic operations to prevent race conditions
- **Scalable**: Can handle multiple concurrent requests without number collisions
- **Persistent**: Generated numbers are stored in Redis for audit and tracking
- **RESTful**: Follows REST principles for easy integration
- **Microservice-Ready**: Designed as a standalone service that can be deployed independently

## Number Format Specifications

### Claim Numbers
- **Pattern**: `Cl-{10-digit-number}`
- **Example**: `Cl-0000000001`, `Cl-0000000002`
- **Counter Key**: `number-generator:claim:sequence`

### Policy Numbers
- **Pattern**: `SG/08/{10-digit-number}`
- **Example**: `SG/08/0000000001`, `SG/08/0000000002`
- **Counter Key**: `number-generator:policy:sequence`

## License

This project is part of a learning exercise and follows standard Spring Boot project structure.

