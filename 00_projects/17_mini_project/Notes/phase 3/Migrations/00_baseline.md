### Base line for Flyway

Everything that exists in my database right now is the starting point. Flyway doesn't need to recreate the past.

The baseline is not a migration that reconstructs the database. It's Flyway's checkpoint saying:


    Existing DB
        │
        ▼
    BASELINE
        │
        │ Flyway starts
        ▼
    01__add_isbn
        │
        ▼
    02__add_indexes


Baseline:
    "I already have this database."

Initial migration:
    "Here's how to build this database from empty."


existing LMS needs the first.
A fresh LMS needs the second.


V0__create_initial_schema.sql