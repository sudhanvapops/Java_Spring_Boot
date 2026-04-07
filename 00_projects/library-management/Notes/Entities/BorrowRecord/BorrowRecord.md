### In a real library, what is a “borrow record”?

1. A borrow record represents:
    - Borrow ID
    - person borrow

    - book Name
    - Author Name 
    all the above will crate reduency instead put book ID

    - borrowDate
    - dueDate
    - returnDate
    - status


2. Is this correct?
👉 “One borrow record = one borrower takes one book at a time”
Yes or No?

WRONG: No One borrower can take many book at a time

If a borrower takes 3 books, you create:
3 BorrowRecords

👉 Each record = 1 borrower + 1 book



3. Identify Core Entities
Who is borrowing?
Borrower

What do you want to call it?
Borrower

4. Relationship Thinking (Very Important)

One borrower can borrow many books over time
👉 So:
Borrower → BorrowRecord = One -> Many 

One book can be borrowed many times (history)
👉 So:
Book → BorrowRecord = Many -> One

5. Business Thinking (Important)

Now think beyond DB:
    When a book is borrowed:
        availableCopies should ↓
        totalCopies stays same
    When returned:
        availableCopies should ↑


### Fields Design

Hibernate handles IDs internally.
In Hibernate, we don’t store IDs manually like that.
Instead we store object references:

Basic Fields:
    Borrow ID
    Borrower
    Book

Dates:
    borrowDate
    dueDate
    returnDate

Status:
    (Borrowed / Returned / Late)
    isLate
    fineAmount


Final Design

id
borrower
book
borrowDate
dueDate
returnDate


And status becomes:
    Derived Logic

public BorrowStatus getStatus() {
    // derived logic
}

status
isLate
fineAmount

Instead:
    Compute them using methods

If user returns late → we may need penalty logic later
calculate everything dynamically

Have minimal invariants (basic safety rules)

### Where should this logic go?

A) Entity - The Model
B) Service - All the Handeling of borrowing and returning books



### Think in terms of:

A) Store status 
B) Calculate status dynamically

You must choose ONE primary source of truth.

Option A: Store Status
Faster queries
Easier filtering (WHERE status = 'BORROWED')
But risk of inconsistency ❌

Option B: Calculate Dynamically
Always correct ✅
No redundancy


### Why NOT ManyToMany ❌

If you do this:

@ManyToMany
Set<Book> books;

👉 You CANNOT store:

borrow date ❌
return date ❌
fine ❌

👉 Hibernate only creates a simple join table:

borrower_book
--------------
borrower_id
book_id

That’s it.


What is the default fetch type of @ManyToOne?
Lazy or Eager?
This matters A LOT for performance

Default: Eger, Change to Lazy for performanace


### Bidirectional Mapping (Now it gets interesting 😈)

What Changes?

Currently:
    👉 BorrowRecord → Book ✔
    👉 BorrowRecord → Borrower ✔

Now we add:
    👉 Book → BorrowRecord
    👉 Borrower → BorrowRecord



Who is the owner now?
BorrowRecord is the Owner

Still:
✔ BorrowRecord (because of @ManyToOne)

👉 mappedBy means:
    “I am NOT the owner”

Why didn’t we add @JoinColumn in Book and Borrower?
Only the side that owns the FK uses @JoinColumn

