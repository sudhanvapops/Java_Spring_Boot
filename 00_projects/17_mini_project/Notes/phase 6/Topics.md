### Block C — Performance Topics

### Topic 1 — C1: N+1 Queries

1. Concepts:
    - What the N+1 problem is
    - Recognizing N+1 from SQL logs
    - Why JPA/Hibernate causes it
    - Fetch Join
    - JOIN FETCH
    - @EntityGraph
    - DTO projections
    - MultipleBagFetchException
    - Fetch join + pagination
    - Choosing the appropriate solution

2. Build/experiment:
    - Create an endpoint that produces N+1
    - Enable SQL/query logging
    - Observe the queries
    - Fix it using different approaches
    - Compare the approaches

3. Not going into:
    - Second-level cache
    - @BatchSize
    - Batch-fetch tuning
    - Statement caching


### Topic 2 — C2: Database Indexes

1. Concepts:
    - Why indexes exist
    - B-tree indexes
    - Sequential scan
    - Index scan
    - EXPLAIN
    - EXPLAIN ANALYZE
    - Selectivity
    - Composite indexes
    - Column ordering in composite indexes
    - Read performance vs write cost

2. Build/experiment:
    - Create a query without an index
    - Run EXPLAIN ANALYZE
    - Observe the sequential scan
    - Add an index
    - Run it again
    - Compare the query plans

3. Not going into:
    - GIN/GiST/BRIN
    - Partial indexes
    - Covering indexes
    - Planner cost-constant tuning
    - pg_stat_statements


### Topic 3 — C3: Load Testing

1. Concepts:
    - Why load testing matters
    - k6
    - Concurrent users/requests
    - Throughput
    - Latency
    - p50
    - p95
    - p99
    - Error rate
    - Connection pool exhaustion
    - Finding the bottleneck from results

2. Build/experiment:
    - Generate concurrent traffic
    - Measure latency and throughput
    - Observe p50/p95/p99
    - Look for errors
    - Investigate connection-pool behavior
    - Create a k6 load test against the LMS
    - Identify the bottleneck


3. Not going into:
    - Distributed load generation
    - JVM GC tuning
    - Kernel-level tuning
    - APM products

### The Progression

C1 — N+1
      ↓
"Why is my application making 101 queries?"

C2 — Indexes
      ↓
"Why is this query slow?"

C3 — Load Testing
      ↓
"What happens when many users hit it simultaneously?"


