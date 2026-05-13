### Borrow Service

it is used to borrow book



### Borrowbook()

What is this method really doing?
A borrower requests a book
The system validates eligibility
The system checks book availability
The system records the borrowing event


Input: cardNumber, isbn
Output: Return the created BorrowRecord (helps debugging + future features)



BEGIN TRANSACTION
  fetch
  validate
  create
  save
COMMIT

If validation fails Roll Back


Find Borrower
Find Book
Validate Available Copies
Check if he already have this copy
Available Copy - 1
New Record
Update List and Set of Book and Borrower add record to both
Persist
Commit transaction

if failed rollback the transaction



### return book

validate borrower
validate book
find active borrow record
update returnDate
increase available copies
commit



### renewBook()


### addBook()


### registerBorrower()


### searchBooks()


### getOverdueBooks()


### getBorrowHistory()


### removeBook()


### addAuthor()

