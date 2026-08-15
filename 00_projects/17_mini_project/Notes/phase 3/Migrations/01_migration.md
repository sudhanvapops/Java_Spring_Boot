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

