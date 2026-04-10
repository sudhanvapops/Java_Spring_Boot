### What uniquely defines a borrower?
id (borrower_id since i already use this name in borrow_record)
email

name
phone

membershipDate

status? (active/suspended)
status is derived



### Relationships

You already know this:

Borrower → BorrowRecord → Book

👉 So:

Question:
What should Borrower have?
Set<BorrowRecord> ?

Still one question 
i am storign each record sepratly inside borrow record 
but i have to store it in a set here why 


### Critical Design Question (Most Important)

👉 Should Borrower directly have:

Set<Book>

OR ONLY:

Set<BorrowRecord>

This is a trap question.

Think carefully:

What happens if you store both?
Will it cause inconsistency?

In borrow record we already store the book ID 
SO no need of Set<Book>
But its not the complete answer but my isninct is sayig its better to store borrowrecord than books since it has all the related details 
so when searched and join the table you will get necissity information along side
and id i do Set<Book>
It only give book, then i have to fecth another call for borrow details

How will you enforce:
    "A borrower cannot borrow more than 5 books"
    by deriving like count of all the current boorow record 
    and applying limit and check

Where does this logic go?
This should go in service

What happens if someone tries to delete a Borrower with history?
Dont delete just disable it like soft delete


### Questions


Q1. Active Borrow Logic
How will you define:
    "currently borrowed books"
    Give exact condition using:

By using return date returnDate we can get count in DAO 
by using that count we can do operations in service layer
we can also use borrowdate but we cannot gurantee wether they returned or not 


Q2. Borrower Entity — Final Fields

borrower_id 
card_no (unique,non_nullable,non mutable)
- can act as PK, But it is exposed to outside world

email (unique,mutable, nullable)
- mutable deafult 

name (non nullable)
phone_no (unique,nullable)

satatus derived


Q3: Should Borrower have helper methods?
Yes 
addBorrowRecord()
removeBorrowRecord()

Beacuse when i add or remove books from the borrwer 
primarily i will be using borrower for the base refrence
and by using this only i can manupilate both BR and Borrower
and also Hibenate wont sync autmoatically on both side

Q4: Where should this logic go:
availableCopies--
Service

Now move on to me coding 
further questions later
also i want this concurrency thinking