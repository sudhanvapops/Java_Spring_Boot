1. Entity (Your Database Models)

2. dao (Data Access Layer)
    This layer handles database operations.
    The layer that talks to Hibernate Sessions.

3. service (Business Logic)
    This layer controls application logic.

DAO only accesses data,
Service decides what should happen.

4. util (Utility Classes)
This is where you store:
    SessionFactory
    Hibernate initialization


### Mental Model

Think of the project like this:

Main
  ↓
Service
  ↓
DAO
  ↓
Hibernate Session
  ↓
Database