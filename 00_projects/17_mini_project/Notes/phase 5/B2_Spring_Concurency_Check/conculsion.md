B2 — Reproduce the race in Spring

Setup:
- One PostgreSQL Testcontainer
- One Spring Boot application context
- 10 concurrent requests
- 10 different members
- One book
- availableCopy = 1

Expected:
- Only one borrow succeeds
- availableCopy = 0

Actual:
- 10 borrows succeed
- availableCopy = 0

Conclusion:
The current implementation allows multiple concurrent
transactions to successfully borrow a book even though
only one copy exists.

The concurrency bug has been reproduced in the
Spring/JPA integration test.