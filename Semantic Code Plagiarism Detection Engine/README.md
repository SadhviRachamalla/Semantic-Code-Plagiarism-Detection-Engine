# Semantic Code Plagiarism Detection Engine

The **Semantic Code Plagiarism Detection Engine** is a production-ready application designed to detect similarity and structure copy-paste behavior in source code files (Java, Python, C++). 

Unlike simple lexical diff engines, this engine normalizes code by parsing it into structural elements (Abstract Syntax Trees or canonical representations), discarding formatting, comments, variable identifiers, and function names. It then combines multiple similarity strategies to compute a weighted plagiarism score.

---

## Technical Stack
- **Java 21** & **Spring Boot 3.2.x**
- **Maven** (Single-module, dependency configuration)
- **Spring Security** (Custom API-key stateless filter)
- **Spring Data JPA** & **PostgreSQL** (Primary database store)
- **Flyway** (Database schema migrations)
- **Spring Redis** (Caching layer for fingerprint computations)
- **JavaParser** (AST parsing adapter for Java)
- **Lombok** & **MapStruct** (Clean domain mapping)
- **Springdoc-OpenAPI / Swagger UI** (Self-documenting APIs)
- **JUnit 5**, **Mockito**, & **Testcontainers** (Containerized integrations)
- **Docker** & **Docker Compose** (Containerization infrastructure)

---

## Architectural Design
The engine is structured under a clean layered design:
- **`parser/`**: Structural AST analysis modules that tokenize Java, Python, and C++ code.
- **`similarity/`**: Contains execution strategies:
  1. **Winnowing (MOSS-style)**: Rolling polynomial hashes and sliding window selections for positional similarity.
  2. **Cosine Vector**: Frequency density distributions of token sequences.
  3. **LCS (Longest Common Subsequence)**: Space-optimized dynamic programming aligning AST sequence patterns.
- **`entity/` & `repository/`**: PostgreSQL JPA persistence.
- **`service/`**: Core orchestrators. Includes `@Async` task pools for bulk checks and Redis interfaces to cache fingerprints.
- **`controller/`**: Secured REST JSON APIs.

---

## Quick Start (Docker Compose)

The easiest way to build and run the database, cache, and application altogether is via Docker Compose:

```bash
# Clean, compile, and spin up the complete container environment
docker-compose up --build
```
Once spun up:
- The REST API will listen at: `http://localhost:8081`
- Auto-generated Swagger documentation will be available at: `http://localhost:8081/swagger-ui.html`

---

## Local Development Setup

To run services locally outside Docker (e.g. while debugging):

### 1. Prerequisite Infrastructure
You need PostgreSQL and Redis. You can launch them using the Docker Compose dependencies:
```bash
# Launch only the databases
docker-compose up -d postgres-db redis-cache
```
*Note: Postgres exposes locally on port `5433` (to avoid conflicts with standard postgres) and Redis exposes on `6379`.*

### 2. Build and Run
Compile the code and execute tests:
```bash
mvn clean install
```
Start the Spring Boot application locally:
```bash
mvn spring-boot:run
```
*The server will start on port `8081`.*

---

## Seeding & Testing the API

To verify that the engine is running and calculating plagiarism accurately:
1. Spin up the engine (using Docker or running locally).
2. Execute the self-contained test script in the `/sample-data` directory:
   ```bash
   python sample-data/seed_data.py
   ```

This script will automatically:
- Create a classroom assignment set.
- Upload 8 sample source files (Java, Python, C++) containing original and plagiarized code.
- Trigger a bulk similarity check.
- Wait for processing and output a detailed comparison match report showing similarity metrics.

---

## Security Headers
Every request to protected endpoints must include:
- `X-API-KEY: reviewer-secret-key-67890` (Reviewer access)
- `X-API-KEY: admin-secret-key-12345` (Admin access)

For endpoint schemas, request payloads, and query parameters, see [API.md](file:///c:/Users/sadhv/Fraud-Ring-Detection/plagiarism-engine/API.md).
