### hibernate: Validate

What exactly is Hibernate validating now that Flyway owns the schema?

Flyway
   ↓
"Is the database schema evolved to the expected version?"

Hibernate ddl-auto=validate
   ↓
"Does my JPA model match the schema that actually exists?"


### Checksum

A checksum is essentially a fingerprint of your migration file.
Flyway calculates a number based on the contents of that migration.

Why does Flyway need checksums?

if you change old migration file
i will throw error or do a reptable migration autmoatically

without it 

if two devolper changed it 
created database history divergence.
Checksum validation catches this early.

### Validation

Validation
Flyway's validation essentially asks:
"Does the migration history in the database still match the migration files in the application?"

It checks things such as:
    - migration exists
    - version matches
    - description matches
    - checksum matches
    - migration hasn't unexpectedly changed


### Delibratly did break

Current version of schema "public": 3
2026-09-07T20:59:53.055+05:30  INFO 2240 --- [library-management-v2] [  restartedMain] o.f.core.internal.command.DbMigrate      : Migrating schema "public" to version "4 - break"
2026-09-07T20:59:53.105+05:30 ERROR 2240 --- [library-management-v2] [  restartedMain] o.f.core.internal.command.DbMigrate      : Migration of schema "public" to version "4 - break" failed! Changes successfully rolled back.


### Exit

Your exit condition

At the end, close your editor/AI assistance and write this from memory:

A.
What exactly happens when Spring Boot starts and Flyway runs?
Give me the sequence.

When spring boot starts it will look migrations
and validates
and checks till how many migrations are applied by using flyway_schema_history
which is created during first run of flyway
then it validates if everything is coorect then it will add the record to schma_histriy
with caluclated checksum

if falied it will give error saying how many migrations appleid where it is now 
and which has failed
and if poosible it will rollback

- Better Answer

Spring Boot starts.
Spring Boot configures the datasource.
Flyway starts and scans the configured migration locations for migration files.
Flyway reads flyway_schema_history.
If it doesn't exist, Flyway creates it.
Flyway compares the migrations it found with the migrations recorded in the history table.
Flyway validates the applied migrations, including their checksums.
If validation succeeds, Flyway determines which migrations are pending.
Flyway executes pending migrations in version order.
After a migration succeeds, Flyway records it in flyway_schema_history, including its version, description, checksum, etc.
If a migration fails, Flyway reports the failure. Depending on the database and migration/transaction behavior, the migration may be rolled back.



B.
Explain the important columns/fields in:
flyway_schema_history


checksum: a  gerated number by the contents of the migration file used to detrmine the changes in the file not unique

success: true or false wier it is applied or not 

description: after __ what is this migartion file name is added there

version: which version it is eg: V4 then version is 4


C.
Answer this production scenario:

A teammate edits an already-applied migration and pushes it. 
Production deployment now fails validation. 
What are your options, and what risk does each option carry?

option to make forward migration since its edited the file undo the changes in file to normal

- better answer

- If an accidentel modifing
- restore back to original content in file

Risk LOW
Safest Option

opt 2

when the change is actually intended
V3 -> orignal
V4 -> forward migtrtion fix


risk
but you have to know how to take it to the safest state by knwoing the current state of production
proper approch

opt 3

repiar
change the cheksum in db to match this

risk:
Potentially high

in flyway its valid
But the SQL in V3 has already been executed in production.
So changing the checksum doesn't magically execute the new SQL.

You can end up with 

DB Schema: Old State
Migration File: New State
Flyway Histry: Says New file is valid



- Leave V3 alone 

If you can answer those without looking things up, Flyway is closed for V3.
The roadmap gives this entire unit a 90-minute box.