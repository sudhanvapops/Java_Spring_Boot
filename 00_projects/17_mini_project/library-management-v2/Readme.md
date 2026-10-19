# Library Management System

A Spring Boot + PostgreSQL REST API for lending books: borrow several at once, return them, and get the fine calculated for you.

![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1-6DB33F) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1) ![Flyway](https://img.shields.io/badge/Flyway-migrations-CC0200)

## The problem

Most library demos are CRUD with a `books` table. A real lending system has to stay correct when things go wrong:

- Two members request the last copy of a book at the same moment.
- A borrow request contains the same book twice, or a book that was soft-deleted.
- A member is at their limit, or has been deactivated.
- A return is late and a fine has to be calculated, and the copy has to become available again.

This project is about getting those rules right and **proving** they hold, with tests against a real PostgreSQL, not mocks.

## What it does

- **Members and books**: create, update, search (books by name or author), soft delete and reactivate.
- **Borrowing**: several books in one transaction. Rejected if the member is inactive, a book is inactive or unavailable, a book is duplicated in the request, the member already holds that book, or the member is at the borrow limit.
- **Returning**: one or many books, fine calculated per late day, available copies restored, summary returned.
- **Configurable rules**: `MAX_BOOKS`, `MAX_BORROW_DAYS` and `FINE_PER_DAY` are stored as settings and changed through the API, not hardcoded.
- **Auth**: JWT-secured endpoints.
- **Errors**: one error shape everywhere (`success`, `errorCode`, `message`, `timestamp`) from a global handler and an `ErrorCode` enum.

Full endpoint list: Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

## Design decisions

**Services throw, a global handler responds.** Early versions built HTTP responses inside the service, which mixed business rules with response formatting. Now services throw typed exceptions (`BookNotFoundException`, `MemberInactiveException`, ...) and a single `@ControllerAdvice` turns them into a consistent response with a stable `errorCode` the frontend can switch on.

**Schema owned by Flyway, checked by Hibernate.** The project started on `ddl-auto=update`. I baselined the existing database and moved all changes into versioned migrations, with `ddl-auto=validate` so the entities and the real schema can't silently drift. A fresh database can be rebuilt from the migration files alone.

**Concurrency is tested, not assumed.** I reproduced the lost-update race in an integration test: one book with `availableCopy = 1`, 10 concurrent borrow requests from 10 different members. Expected one success; the original code let **all 10 succeed**.
I compared three fixes: pessimistic locking (`SELECT ... FOR UPDATE`), `REPEATABLE READ` with retry on serialization failure, and an atomic conditional `UPDATE ... SET available_copy = available_copy - 1 WHERE id = ? AND available_copy > 0` that checks the affected row count.
<!-- TODO: state which one you shipped and why, in 2 sentences, and confirm the same test now passes (1 success, 9 rejected). -->

**Real Postgres in tests.** Integration tests run against a Testcontainers PostgreSQL 16, so locking and isolation behaviour is the real thing.

**Measure before optimising.** The N+1 audit and the load test below exist so that every performance change is compared against a fixed baseline, with the same dataset and the same k6 scripts.

## Performance

Dataset: 100,000 books, 10,000 members, 50,000 borrow transactions, 100,000 borrow records. Load: k6, 5 virtual users per endpoint, 30 s each.

The messurments are the median of 4 runs in k6
with 5 VUs and for 30 seconds

1st run without cahce in postgres
2 - 3 runs are chaced in postgres
noadditional cahcing services are added

| Endpoint | median | p95 | p99 | SQL statements per request |
|---|---|---|---|---|
| Author search | 23.1 ms | 32.3 ms | 37.5 ms | **1 + 1  (auth/jwt filter)**  |
| Member borrow records | 45.9 ms | 53.5 ms | 64.1 ms | **1 ( jwt ) + 1 + 1 + 9 + 7 = 19**  |
| Member transactions | 68.0 ms | 80.6 ms | 102.9 ms | **8 (1+N pattern)** |


An audit of the service layer found seven confirmed N+1 patterns, mostly in the borrow record and transaction services, caused by lazy loading while mapping entities to DTOs.

| Endpoint | SQL statements per request | After Fixing |
|---|---|---|
| Get All records  | **~150k + 1 (auth/jwt filter)**  | **2** |
| Member borrow records | **1 ( jwt ) + 1 + 1 + 9 + 7 = 19**  | **2** |
| Member transactions | **8 (1+N pattern)** | **2** |


Caveat: the app, the database and k6 share one laptop, so absolute numbers are machine-specific. Only before/after comparisons on the same machine mean anything.

## Architecture

```text
Client (Swagger / Postman / k6)
        │
REST controllers          ← request/response only
        │
Service layer             ← business rules, transactions, throws typed exceptions
        │
Spring Data JPA repos
        │
PostgreSQL (schema managed by Flyway)
```

## Run it

### Clone Only This Project (Sparse Checkout)

```bash
git clone --filter=blob:none --no-checkout https://github.com/sudhanvapops/Java_Spring_Boot.git
cd Java_Spring_Boot

git sparse-checkout init --cone
git sparse-checkout set 00_projects/17_mini_project/library-management-v2

git checkout main
cd 00_projects/17_mini_project/library-management-v2
```

Set the datasource in `application.yml`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/<db>
spring.datasource.username=<user>
spring.datasource.password=<password>
```

```bash
mvn spring-boot:run     # Flyway applies migrations on startup
mvn test                # integration tests need Docker for Testcontainers
mvn spring-boo:test-run # running test app with profile: perf
```

## What I'd do differently

1. **Design for concurrency from day one.** The first version passed every Postman test and was still wrong under concurrent load. A passing happy path told me nothing about the invariant "available copies never go below zero".
2. **Start with Flyway.** Baselining a live schema later was more work than writing `V1` at the beginning.
3. **Fetch strategy up front.** I mapped entities to DTOs in a loop and only found the N+1 patterns by auditing afterwards. I'd write the query for each endpoint deliberately and assert the statement count in a test.
4. **Paginate list endpoints early.** `/all` endpoints return everything, which is fine at 5 rows and not at 100,000.
5. **Run load tests on separate machines.** One laptop running app, database and load generator makes small differences impossible to trust.
