### What happens during Spring Boot startup?


Spring Boot starts
       ↓
Flyway starts
       ↓
Connect to PostgreSQL
       ↓
Find migration files
       ↓
Find flyway_schema_history
       ↓
Compare them
       ↓
Run pending migrations
       ↓
Application continues startup


The important conceptual point is:
    The application should not happily pretend that the database is in the expected state.


