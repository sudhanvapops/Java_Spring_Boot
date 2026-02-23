### Big Picture 

Hibernate sits between your Java objects and the database.

You write:
Student s = new Student();

Hibernate converts it into:
INSERT INTO student ...

### So Hibernate needs 3 things:
1. Database configuration
2. Mapping between Java class ↔ Table
3. Safe way to talk to DB repeatedly

That’s exactly what these objects solve:
Configuration → SessionFactory → Session → Transaction

Blueprint → Factory → Worker → Work Order


### 1. Configuration (Configuration cfg)
Configuration cfg = new Configuration();
cfg.configure();

Hibernate must know:
    Database URL
    Username/password
    Dialect (MySQL / PostgreSQL)
    Driver
    Mapping classes

All this lives in: hibernate.cfg.xml
Configuration = Load Hibernate settings

Internally it does:
    Read XML
    Load DB configs
    Prepare ORM mappings
    Prepare metadata


### 2. addAnnotatedClass(Student.class)

Hibernate must know:
    Which Java classes represent database tables?

Your class:
    @Entity
    @Table(name="student")
    class Student {}


### 3. SessionFactory
SessionFactory sf = cfg.buildSessionFactory();
VERY IMPORTANT OBJECT.

Creating DB connections is EXPENSIVE.

You cannot do:
    connect DB
    disconnect
    connect DB
    disconnect

for every query.

So Hibernate creates:
    One heavyweight factory

that:
    manages connection pool
    caches metadata
    optimizes SQL
    prepares mappings

Create ONLY ONE per application.
Application Start → create SessionFactory
Application End → close SessionFactory


### 4.Session

Session represents:
    One conversation with database

It is:
    lightweight
    short lived
    not thread-safe

Used for:
    save
    update
    delete
    fetch
    query

Session = Worker using factory tools
Each request/user/action → new session.


### 5. Transaction

WHY Transaction?
Database operations must be atomic.

Either:
    ALL SUCCESS ✅
    OR
    ROLLBACK ❌

Example:
    Insert Student
    Update Fees
    Create Log

If step 3 fails → everything undo.

So Hibernate forces:
    Start Transaction
    Do work
    Commit


### 6. persist()
Hibernate now:
    Tracks object
    Converts object → SQL
    Executes during commit

Hibernate works using Persistence Context
It tracks object state automatically.

### Full Flow

Configuration
     ↓
Load DB + Entities
     ↓
SessionFactory (ONE)
     ↓
Session (per work)
     ↓
Transaction
     ↓
Persist Object
     ↓
Commit → SQL executed

### Hibernate architecture exists because:
    ✅ DB connections are costly
    ✅ Metadata parsing is costly
    ✅ Thread safety needed
    ✅ Performance optimization required

So Hibernate separates responsibilities.
When you move to Spring Boot, all this disappears:
