### STEP 1 — Seed Data

Think:
    “What world am I creating?”


Maybe:
    3 authors
    5 books
    2 borrowers



### Relationships

Now think deeper:

Which books have multiple authors?

This forces you to think about:
    owning side
    helper methods
    relationship synchronization


### Copies

Which books should have:
only 1 copy?
many copies?
zero available copies?

Now you can test:

borrow failure
successful borrow
concurrency-like situations


### Borrowers

Think of:

one normal borrower
one borrower already holding books

Why?

Because later you’ll test:

duplicate borrowing
borrowing limits
active records

