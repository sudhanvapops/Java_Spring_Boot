A system that tracks books, people, and borrowing activity reliably

### Core Responsibility of this System

1. Data (Entities)
Book
Author
Borrower
BorrowRecord // History of borrowers

2. Business Rules (Logic)
A book cannot be borrowed if unavailable
A borrower can have limited books at a time
A borrow record must have borrow date + return date
A book must have an author



Answer these:

Can one Author have multiple Books?
Yes
Can one Book have multiple Authors?
Yes

This is:
    Many-to-Many


BorrowRecord is a "bridge" between Borrower and Book

One Borrower → Many BorrowRecords 
Each BorrowRecord → One Borrower 

Can one Borrower have multiple BorrowRecords?
Yes

Can one Book appear in multiple BorrowRecords?

yes it can come in multiple student Borrow Record

| id | borrower_id | book_id | borrow_date | return_date |
| -- | ----------- | ------- | ----------- | ----------- |
| 1  | 101         | 10      | ...         | ...         |
| 2  | 101         | 15      | ...         | ...         |
| 3  | 102         | 10      | ...         | ...         |


Author ↔ Book → Many-to-Many
Borrower → BorrowRecord → One-to-Many
Book → BorrowRecord → One-to-Many