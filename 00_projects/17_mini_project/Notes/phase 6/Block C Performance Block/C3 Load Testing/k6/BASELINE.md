# LMS v2 — C3 Load-Test Baseline

Purpose: the fixed "before" measurement. Every C1 (N+1) and C2 (index) change is compared against this file using the SAME scripts and the SAME dataset. An improvement only counts if it is larger than the noise floor recorded below.

Fields marked `TODO` are filled in from my own runs. Do not edit the numbers after C1/C2 start.

---

## 1. Environment

| Item | Value |
|---|---|
| App | Spring Boot 4.1.0, Java 21, Hibernate 7.4.1, run locally via `mvn spring-boot:test-run` (`PerfApplication`, profile `perf`) |
| Database | PostgreSQL 16 (`postgres:16-alpine`) in a Testcontainers container on Docker Desktop (7.3 GB RAM) |
| Load generator | k6 on the same Windows machine as the app and DB |
| Connection pool | HikariCP, pool size not set explicitly (default 10) |
| `open-in-view` | enabled (default) |
| Access token lifetime | ~300 s in perf profile (default is 60 s) |
| SQL logging | OFF during all k6 runs |
| Machine notes | TODO (other heavy apps closed? yes/no) |

## 2. Dataset (identical for every run)

Seeded by `PerfDataSeeder` (set-based SQL, `setseed(0.42)`), followed by `ANALYZE`.

| Table | Rows | Shape |
|---|---|---|
| book | 100,000 | `author 0`..`author 4999` (~20 books per author) |
| member | 10,000 | ids 1..10000 |
| borrow_transaction | 50,000 | 5 per member |
| borrow_record | 100,000 | 2 per transaction (10 per member) |

Plus the 5 books / 5 members / admin / settings from `DataSeeder`.

## 3. Endpoints under test

| k6 tag | Endpoint | Expected response |
|---|---|---|
| `author_search` | `GET /api/book/author?author=author N` | ~20 books |
| `member_records` | `GET /api/borrowrecord/member-id/{id}` | 10 rows |
| `member_transaction_records` | `GET /api/borrow-transactions/member-id/{id}` | 5 rows |

## 4. SQL statements per request (observed in Hibernate log, one Swagger call each)

| Endpoint | Statements | Notes |
|---|---|---|
| `member_transaction_records` | **8** | 1 user lookup (JWT filter, every request) + 1 member + 1 transactions + 5 `borrow_record` (one per transaction, `left join book`) → classic 1+N |
| `member_records` | TODO | |
| `author_search` | TODO | |

## 5. Baseline latency (k6, 5 VUs per scenario, 30 s each, `sleep(0.5)` think time)

Scripts: `baseline.js`, run via `run-baseline.ps1`, summarized via `summarize.ps1`. Checks must be 100% and failures 0 in every counted run.

### 5.1 First valid run (single run, kept for reference)

| Endpoint | median | p95 | p99 |
|---|---|---|---|
| `author_search` | 23.1 ms | 32.3 ms | 37.5 ms |
| `member_records` | 45.9 ms | 53.5 ms | 64.1 ms |
| `member_transaction_records` | 68.0 ms | 80.6 ms | 102.9 ms |

Checks 100%, `http_req_failed` 0%, 975 requests. (Max latency of ~1 s overall was the login in `setup()`, not an endpoint.)

### 5.2 Three-run baseline (official numbers)

| Endpoint | p95 run1 | p95 run2 | p95 run3 | **Baseline p95 (median of 3)** | **Noise (max − min)** |
|---|---|---|---|---|---|
| `author_search` | TODO | TODO | TODO | TODO | TODO |
| `member_records` | TODO | TODO | TODO | TODO | TODO |
| `member_transaction_records` | TODO | TODO | TODO | TODO | TODO |

Rule: a later improvement smaller than the noise value is not a real improvement.

## 6. Known caveats

- App, Postgres (Docker Desktop VM), and k6 share one laptop, so absolute numbers are machine-specific. Only before/after comparisons on this machine are meaningful.
- Only 10 members' worth of per-member fan-out is realistic here (5 transactions, 10 records each); N+1 cost grows with transactions per member.
- 50k–100k rows is small for index effects (C2). If an index shows no gain, scale total rows (not per-member counts) and re-baseline.
- The first run after an app restart is cold (JIT, DB cache); always do a throwaway run first.
