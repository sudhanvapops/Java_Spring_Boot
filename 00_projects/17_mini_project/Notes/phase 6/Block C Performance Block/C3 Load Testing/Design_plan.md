### Generating Tests

./mvnw spring-boot:test-run
   ├─ Testcontainers starts postgres:16-alpine (Docker)
   ├─ Flyway migrates, your perf seeder inserts the data
   ├─ app listens on localhost:8080 (local machine)
   └─ you run `k6 run ...` in another terminal (local machine)



### Test Data

100k books, 10k members, 50k transactions (5 per member), and 100k records (2 per transaction). The “get all” endpoints would return 50k+ transactions in one response, so for the N+1 baseline target the member-scoped endpoints


