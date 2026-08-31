# Ledger Application

A financial ledger REST API built with Spring Boot that manages deposits and withdrawals with transaction history tracking.

## Overview

The Ledger Application is a lightweight, thread-safe service for managing financial transactions. It provides REST endpoints to record deposits/withdrawals, query current balance, and retrieve transaction history.

## Features

- ✅ **Transaction Recording** - Record deposits and withdrawals with automatic balance updates
- ✅ **Balance Inquiry** - Retrieve current account balance at any time
- ✅ **Transaction History** - View complete audit trail of all transactions
- ✅ **Error Handling** - Graceful error responses with appropriate HTTP status codes
- ✅ **Monetary Precision** - Uses `BigDecimal` for accurate financial calculations

## Technology Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 26 | Language |
| Spring Boot | 4.1.1 | Framework |
| Maven | 3.x | Build Tool |
| JUnit 5 | Latest | Testing Framework |
| Mockito | Latest | Test Mocking |

## Installation & Setup

### Prerequisites

- **Java 26+** installed and in PATH
- **Maven 3.6+** or use the included Maven wrapper

### Clone & Build

```bash
# Clone the repository
cd legendary-ledger

# Build the project
mvn clean compile
```

### Run the Application

```bash
# Using Maven
mvn spring-boot:run
```
The application will start on **http://localhost:8080**.

### Run Tests

```bash
# Run all tests
mvn clean test
```

## API Documentation

### Base URL 
http://localhost:8080/api/ledger

### Endpoints

#### 1. Record a Transaction
```http
POST /api/ledger/movements
Content-Type: application/json

{
  "type": "DEPOSIT",
  "amount": 100.50
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "type": "DEPOSIT",
  "amount": 100.50,
  "timestamp": "2026-08-31T10:30:45.123Z"
}
```

**Possible Errors:**
- `400 Bad Request` - Invalid transaction type or missing fields
- `422 Unprocessable Entity` - Business rule violation (e.g., insufficient balance for withdrawal)

---

#### 2. Get Current Balance
```http
GET /api/ledger/balance
```

**Response (200 OK):**
```json
{
  "balance": 250.75
}
```

---

#### 3. Get Transaction History
```http
GET /api/ledger/history
```

**Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "type": "DEPOSIT",
    "amount": 100.00,
    "timestamp": "2026-08-31T10:15:00.000Z"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "type": "WITHDRAWAL",
    "amount": 25.50,
    "timestamp": "2026-08-31T10:30:45.123Z"
  }
]
```


## Assumptions and Design Decisions

### 1. **Single-wallet ledger**
- **Decision**: The ledger would consist of a single `wallet` instead of a multi-wallet system.
- **Rationale**: The requirements did not mention creating or managing multiple wallets, or associating transactions with an account.
- **Implication**: The project is easy to develop in a small time and I can showcase my technical approach and best practices that I implement.

### 2. **In-Memory Storage**
- **Decision**: Transaction history and balance are stored in-memory using `ArrayList` and `BigDecimal`.
- **Rationale**: Suitable for a lightweight, stateless service. For production use with persistence requirements, this should be replaced with a database (PostgreSQL, MySQL) and a repository layer.
- **Implication**: Data is lost on application restart; not suitable for financial systems with audit requirements.

### 3 **Thread Safety via Synchronization**
- **Decision**: `recordMovement()` and `getBalance()` are marked `synchronized`.
- **Rationale**: Ensures thread-safe access when multiple requests modify balance simultaneously. Prevents race conditions during concurrent deposits/withdrawals.
- **Limitation**: Synchronization can become a bottleneck at high concurrency. For production, consider optimistic locking or distributed locks (e.g., Redis).

### 4. **BigDecimal for Monetary Values**
- **Decision**: All monetary amounts use `BigDecimal` instead of `double` or `float`.
- **Rationale**: Prevents floating-point precision errors critical in financial calculations.
- **Validation**: Input validation ensures amounts are positive and non-zero.

### 5. **Immutable Transaction Records**
- **Decision**: `Transaction` is implemented as a Java `record` (immutable).
- **Rationale**: Guarantees transaction data integrity; prevents accidental mutations after creation.
- **Benefit**: Thread-safe by design without additional synchronization.

### 6. **DTO Separation (Request & Response)**
- **Decision**: Request (`MovementRequest`) and Response (`TransactionResponse`) DTOs are separate from the domain model (`Transaction`).
- **Rationale**: Decouples API contract from internal implementation, allowing model changes without breaking client contracts.
- **Benefit**: Enables future API versioning and validation-specific annotations.

### 7. **Validation at Controller Level**
- **Decision**: Input validation uses Jakarta validation annotations `@NotNull`, `@Positive` with `@Valid`.
- **Rationale**: Catches invalid requests early, before they reach the service layer.
- **HTTP Status**: Validation errors return `400 Bad Request`; business rule violations return `422 Unprocessable Entity`.

### 8. **No Audit Logging (Current)**
- **Decision**: Transactions are recorded but not logged to external systems.
- **Rationale**: Suitable for a proof-of-concept. Production systems should log all transactions for compliance, debugging, and auditing.
- **Future**: Add SLF4J/Logback for comprehensive transaction and error logging.
