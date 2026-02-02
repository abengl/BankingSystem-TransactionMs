# Transaction Microservice - Banking System

A reactive microservice for managing financial transactions in a distributed banking system. Built with Spring WebFlux for non-blocking, high-throughput transaction processing with MongoDB for flexible data storage.

## 🎯 Overview

This microservice handles all transaction operations including transfer registration, transaction history, and real-time balance updates. Implements reactive programming patterns for scalability and integrates with the Account microservice for atomic transfer execution.

## 🏗️ Architecture & Design

### Key Features
- **Reactive Architecture**: Built with Spring WebFlux for non-blocking, asynchronous operations
- **Contract-First API Design**: OpenAPI 3.0 specifications with reactive patterns (Mono/Flux)
- **Event-Driven Processing**: Reactive streams for high-throughput transaction handling
- **NoSQL Persistence**: MongoDB for flexible, scalable transaction storage
- **Microservice Orchestration**: Coordinates with Account service for transfer execution
- **Real-time Processing**: Reactive chains for immediate transaction validation and processing

### Technology Stack
- **Framework**: Spring Boot 3.5.7 with WebFlux
- **Language**: Java 17
- **Database**: MongoDB (Reactive Driver)
- **Reactive Core**: Project Reactor (Mono/Flux)
- **API Documentation**: OpenAPI 3.0 (Swagger UI for WebFlux)
- **Testing**: JUnit 5, Mockito, Reactor Test
- **Code Quality**: JaCoCo (70% coverage), Checkstyle
- **Build Tool**: Maven

## 📋 API Endpoints

### Transaction API (`/api/v1/transactions`)
- `GET /` - Retrieve all transactions
- `GET /{transactionId}` - Retrieve transaction by ID (MongoDB ObjectId)
- `GET /account/{accountId}` - Get transaction history for an account
- `POST /transfer` - Register and execute a transfer transaction

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- MongoDB 4.4+ (or MongoDB Atlas)

### Local Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/abengl/BankingSystem-TransactionMs.git
   cd transaction-ms
   ```

2. **Configure MongoDB**
   ```properties
   # application.properties
   spring.data.mongodb.uri=mongodb://localhost:27017/transaction_db
   
   # Account service URL for transfer execution
   account.service.url=http://localhost:8086/api/v1/internal/accounts
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The service will start on `http://localhost:8087`

### API Documentation

Access interactive API documentation at:
- **Swagger UI**: [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html)
- **OpenAPI Spec**: [http://localhost:8087/v3/api-docs](http://localhost:8087/v3/api-docs)

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Test Reactive Flows
```bash
mvn test -Dtest=TransactionServiceTest
```

### Generate Coverage Report
```bash
mvn clean test jacoco:report
```
View report at `target/site/jacoco/index.html`

### Code Quality Check
```bash
mvn checkstyle:check
```

## 📂 Project Structure

```
transaction-ms/
├── src/main/java/com/alessandragodoy/transactionms/
│   ├── api/                    # Generated reactive API interface
│   │   └── TransactionApi.java
│   ├── controller/             # Reactive controller implementation
│   │   └── TransactionController.java
│   ├── service/               # Reactive business logic
│   │   ├── TransactionService.java
│   │   └── impl/
│   │       └── TransactionServiceImpl.java
│   ├── dto/                    # Data Transfer Objects
│   ├── model/                 # MongoDB documents
│   ├── repository/            # Reactive MongoDB repository
│   ├── adapter/              # External service clients (WebClient)
│   ├── exception/            # Custom exceptions
│   └── utility/              # Helper classes
├── src/main/resources/
│   ├── openapi/              # API contract
│   │   └── transaction-api.yml
│   └── application.properties
└── pom.xml
```

## 🔗 Integration

### Microservice Communication

This service integrates with:
- **Account Microservice**: Executes balance transfers via reactive WebClient


## 📊 Transaction Processing

### Transaction Types
- **TRANSFER_OWN_ACCOUNT**: Transfer between accounts owned by same customer
- **TRANSFER_THIRD_PARTY_ACCOUNT**: Transfer to different customer's account

### Transaction Status
- **PENDING**: Transaction initiated but not completed
- **COMPLETED**: Transfer successfully executed
- **FAILED**: Transfer execution failed

### Processing Flow
1. **Validation**: Validate request parameters (amount > 0, accounts different)
2. **Account Verification**: Verify both accounts exist and are active
3. **Balance Check**: Confirm source account has sufficient funds
4. **Transaction Record**: Save transaction to MongoDB with PENDING status
5. **Transfer Execution**: Call Account service to update balances atomically
6. **Status Update**: Update transaction status to COMPLETED or FAILED


## 📊 Code Quality Metrics

- **Test Coverage**: Minimum 70% line and instruction coverage
- **Reactive Testing**: StepVerifier for Mono/Flux testing
- **Code Style**: Google Java Style Guide compliance
- **Excluded from Coverage**: Configuration, DTOs, generated code, exceptions

## 🎓 Technical Highlights

- **Reactive Programming**: Non-blocking I/O with Project Reactor (Mono/Flux)
- **Contract-First Development**: OpenAPI 3.0 with reactive patterns
- **NoSQL Database**: MongoDB for flexible, scalable storage
- **WebClient Integration**: Reactive HTTP client for microservice communication
- **Functional Composition**: Reactive chains for complex transaction flows
- **Error Resilience**: Comprehensive error handling in reactive streams
- **Type-Safe APIs**: Generated interfaces with reactive signatures
- **Event-Driven**: Asynchronous processing for high throughput

## 🔍 Reactive Advantages

- **Scalability**: Handles thousands of concurrent transactions with minimal threads
- **Responsiveness**: Non-blocking I/O ensures low latency
- **Backpressure**: Reactive Streams protocol handles flow control
- **Efficiency**: Thread utilization optimized for I/O-bound operations
- **Resilience**: Built-in error handling and retry mechanisms

## 📫 Contact

**Alessandra Godoy**
- Email: api@alessandragodoy.com
