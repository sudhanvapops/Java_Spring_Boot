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

