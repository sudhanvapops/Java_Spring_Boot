### Versioned Migration

V1__Desc_ription.sql

created flyway_schema_history in db
based on cheksum

baseline: use this as the base version


### Repetable Migrations

- migrations applied every time cheksum changes
- they are applied last
- used to craetae or update views, procedure or bulk adding of data
- Nameing convention

R__next_hfsdh.sql


### Baseline

- i have a db
- and flyway was never used
- introduce Flyway into the project.

- flyway looks at db and asks where did this start
- you can say baseline-version: 0 in yml


meaning: Consider version 0 as the starting point of this existing database

It's a marker in Flyway's history.
not run this 


### Baseline Migration

| Thing                  | Example                  | Purpose                                                   |
| ---------------------- | ------------------------ | --------------------------------------------------------- |
| Versioned migration    | `V3__add_isbn.sql`       | Apply a normal schema change                              |
| Baseline configuration | `baseline-version: 0`    | Mark an existing DB as starting from a version            |
| Baseline migration     | `B5__initial_schema.sql` | Efficiently establish a baseline schema for new databases |


Example:

suppose i have

V1
V2
V3
...
V100
V101
V102
...
V200

A completely fresh database normally has to run: V1 - V200
That's potentially a lot of migrations.

Instead, you can create a baseline migration:
    B200__initial_schema.sql

which represents: Here is what the database should look like after all migrations through V200.

- naming convection
B(VERsion)__desc.sql

- sp you can tell start from here and ignore everythign behind this

### Undo Migrations

- comercial only avaialble
- naming convention
U(version corresponds)__desc

- whole migration succeded and want to undo 
- or failed its not helpful


- better use forward migrations

### Conditional Statements 

and use them to run before migration 
but drect support is not there 



### Failed Migrations

if in the middle something failed

- if it was transactional it will rollback

For databases that support transactional DDL for those operations, Flyway can roll back the migration as a transaction.

For operations that aren't fully transactional, you can end up with partial changes.


- So you could potentially have:

    publisher        ✅ exists
    publication_year ✅ exists
    third operation  ❌ failed

- while Flyway records:
    V3
    success = false


- continue as if nothing happened.

You need to determine:
    What actually happened in the database?
    Did the migration partially execute?
    Why did it fail?
    Can the problem be fixed safely?
    What should the migration history look like afterward?


Example:
    any constraints failed
    like age > 18 failed 
    due to a record already exits < 18

If the DB is Partailly changed


### REPAIR

This is where Flyway's repair command becomes relevant.
Conceptually:
    repair fixes Flyway's bookkeeping/history metadata.

it does not magically undo your database changes.


so it will cleanup metadata that in flyway_schema_histry
dont undo the sql actaul table

Decide the migration strategy

Depending on the state and environment, you may:
    correct and rerun the migration
    clean up the partial database change
    create a new migration
    use repair to correct Flyway's history metadata

The correct choice depends on what actually happened.


### Golden Rule

- Once a migration has been applied, treat it as immutable.

depidng bu do a forward migartion in production



### Comands


1. Through Spring Boot — migrations run automatically when the application starts.
2. Through Maven Flyway commands — you explicitly run commands such as migrate, validate, info, etc.

mvn flyway:migrate 
for apply pending migrations

mvn flyway:info 
Shows Flyway's understanding of your migration history

validate
Checks whether your migration files still match the migration history.

repair
Repairs Flyway's schema history table.


clean
⚠️ VERY DANGEROUS
mvn flyway:clean
It drops objects managed by Flyway.

apply to real files in migration folder

This is mainly useful for development/testing.
Don't casually run this against production.

