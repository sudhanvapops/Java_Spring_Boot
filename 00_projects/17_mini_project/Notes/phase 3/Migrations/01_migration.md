### 1 migration

adding isbn to books


### What should happen to existing books?

You already have existing book data, and you're adding a new column.
If isbn is going to be NOT NULL, existing rows need ISBN values first.

- So we need to decide whether:

isbn nullable initially
or
add isbn

populate existing rows
then make isbn NOT NULL

Don't blindly make it NOT NULL yet.


### Possibalities

- Add isbn as nullable first, then populate existing rows, then make it NOT NULL.
- Add the column with a temporary/default value.
- Add it as nullable and leave it nullable.
- Something else think is more appropriate.


- You have real development data already. You need to decide whether
- you know the real ISBN for each existing book and will populate those values, or

- these development books don't have meaningful ISBNs, in which case you need a different migration strategy.

Should ISBN itself be unique?
Yes


1. Populating the existing 6 books

All three approaches you mentioned are technically possible:

Frontend form → update each book
PATCH/PUT requests → update each book
One script/SQL operation → populate all existing rows in one transaction

For a database migration, I'd prefer the third approach.

Why?

These ISBNs are data required to complete the schema migration. Making you manually call your API six times couples the migration to your application/API being operational.



A better migration flow is:

V1
Add isbn as nullable
        ↓
Populate existing ISBNs
        ↓
Verify no NULLs
        ↓
Add UNIQUE constraint
        ↓
Make isbn NOT NULL
