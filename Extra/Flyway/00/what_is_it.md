### What is Flyway

database migration tool that helps you manage and version changes to your database schema in a reliable, repeatable way. 
It is widely used in DevOps and CI/CD pipelines to keep databases synchronized
across development, testing, staging, and production environments.

### What Problem is it solving

- For my LMS i can wrtite manual query and run it to migrate
- since its samll it can run

But When i have multiple environemts
ie
    Your laptop DB
    Development DB
    Testing DB
    Staging DB
    Production DB


I have done:
    V1 → create books
    V2 → add isbn
    V3 → add available
    V4 → add index

have to remember:
    "Did I run V2 on staging?"
    "Did I run V3 on production?"
    "Wait... did I already run that SQL?"
    "Which version is production on?"

Flyway solves this

Flyway gives your SQL a memory

I write the files of migration

and it has Something like

| Version | Migration      | Status  |
| ------- | -------------- | ------- |
| V1      | create_books   | Success |
| V2      | add_isbn       | Success |
| V3      | add_available  | Success |
| V4      | add_book_index | Success |

Now Flyway knows:
    "I've already executed V1, V2 and V3. Only V4 is new."

Now Git contains my migration file:

Deploy application
       ↓
Flyway
       ↓
Check flyway_schema_history
       ↓
V5 hasn't run
       ↓
Execute V5
       ↓
Record V15 as successful


don't have to SSH into the production database and manually remember:
    "Oh yeah, I need to run this SQL first."
    After a while if i have 12 Versions


don't want someone joining the project to ask:
    "Okay, which 12 SQL files do I need to run, and in what order?"

with flyway with one command
it constructs recent db with empt db


- it also checks the if prev migration files have been changed

- If two developers add new migration files
- two new migrations has created
- when deployed 
- the Flyway automatically applies it without conflict

### Flyway

You
 ↓
Write SQL migration
 ↓
Commit to Git
 ↓
Flyway
 ↓
Checks database history
 ↓
Runs only what's missing
 ↓
Records it

### Why use Flyway?

1. Without Flyway:
    - Developers manually run SQL scripts.
    - Different environments can drift out of sync.
    - It's difficult to know which schema version each database is using.

2. With Flyway:
    - Every schema change is tracked in source control.
    - Database deployments become automated and repeatable.
    - Teams can collaborate on database changes with less risk of conflicts.


### Example with Scenerios

### Scenario 1

### Example 1

- Both changes
- wirte new filed in application
- wirte new filed in database
- you forget to do in databse
- There fly way comes when you deploy Application
- it checks the migration file automtcialy applies it 


### Example 2

- V1
application
Book{
    id
    title
    isbn
}

DB:
    id
    title
    isbn

- V2 
Application:
Book{
    id
    title
    isbn_number
}

DB:
    id
    title
    isbn_number

Now application V2 has bugs
roll back to V1
Db cannot be roolbacked like git


application roll back to V1
now it wants isbn but db has isbn_number

so to avoid it you add both to db
isbn
isbn_number
new writes goes to isbn_number
while still suporting isbn

when confident 
move the isbn entires to isbn_number
and delete the isbn


### Scenerio 2: Multiple servers

When a application is using 
multiple servers

Server 1: V1
Server 2: V1
Server 3: V1

Deployment begins

You don't necessarily shut down A, B and C simultaneously.

deploy V2 to A first:

Load Balancer
     |
 ┌───┼────┐
 ↓   ↓    ↓
 A   B    C
 V2  V1   V1

So now:
A (V2) → expects isbn
B (V1) → doesn't care about isbn
C (V1) → doesn't care about isbn


Then B and C

Therefore the database migration has to be designed so that both application versions can coexist temporarily.

Fliway handeled the migration part from
V1 of DB not applicaition ABC to V2

