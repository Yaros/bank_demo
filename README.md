# Workspace Bank

A workspace containing backend and frontend projects.

## Structure

- `backend/` - Backend services
- `frontend/` - Frontend application ( [see `/frontend/bank-ui/README.md`](./frontend/bank-ui/README.md) )


# Bank Account Management Application

## Overview

This project is a simple banking application developed as a homework assignment.

The backend is implemented as a REST API using Java and Spring Boot. It allows users to:

* Manage bank accounts
* Deposit money
* Debit money
* Exchange funds between accounts with fixed exchange rates
* View account balances
* Retrieve transaction history with pagination

For simplicity, authentication is out of scope. The /api/accounts endpoint returns the accounts of the current (mock) user.

The application stores data in an H2 SQL database.

### Assumptions

* Fixed exchange rates are hardcoded/configured for demonstration purposes.
* The application assumes a single current user (authentication is out of scope).
* H2 is used only for demonstration; the persistence layer is database-agnostic.

## Technology Stack

### Backend

* Java 21
* Spring Boot 4.0.7
* Spring Web
* Spring Data JPA
* Spring Validation
* H2 Database
* Flyway
* OpenFeign
* MapStruct
* Lombok
* Springdoc OpenAPI
* JUnit 5
* Mockito

### Build Tool

* Gradle (Kotlin DSL)

## Project Structure

```
src/main/java/com/example/demo/bank
 ├── account
 │    └── dto
 ├── common
 │    ├── domain
 │    ├── dto
 │    ├── exception
 │    └── persistence
 │         └── converter
 ├── exchanage
 ├── external
 ├── transaction
 │    └── dto
 └── user
```

## Running the Application

Clone the repository:

```bash
git clone <repository-url>
cd backend/demo
```

Start the application:

```bash
./gradlew bootRun
```

or

```bash
./gradlew test
./gradlew bootJar
java -jar build/libs/demo.jar
```

## API Documentation

After starting the application:

* Swagger UI:
  `http://localhost:8080/swagger-ui.html`

* OpenAPI:
  `http://localhost:8080/v3/api-docs`

## Health status

After starting the application:

  `http://localhost:8080/actuator/health`

## Database

The application uses an in-memory H2 database.
Database schema is managed with Flyway.

## Running Tests

Execute:

```bash
./gradlew test
```

## Design Decisions

* Each account has exactly one currency.
* Monetary values are represented using `BigDecimal`.
* Currency exchange uses configurable fixed exchange rates.
* Every balance-changing operation creates a transaction record.
* Debit operations simulate an external logging service using HTTP.
* Transaction history supports pagination for efficient loading.
* REST controllers are separated from business logic through service classes.
* DTOs are mapped using MapStruct.

## Possible Improvements

Given more time, the application could be extended with:

* Authentication and authorization
* Optimistic locking for concurrent updates
* Testcontainers instead of H2 for integration tests
* Docker and Docker Compose support
* Caching of exchange rates
* Asynchronous external logging
* CI pipeline (GitHub Actions)
* Better monitoring (Micrometer + Prometheus)
* Rate limiting
* Multi-user support



## Author

Jaroslav Mashirin
