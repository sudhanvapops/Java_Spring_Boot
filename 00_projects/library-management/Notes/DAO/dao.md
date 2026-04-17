### DAO should do one thing only:

Talk to the database

DAO Responsibilities:
    save(entity)
    findById(id)
    findByCardNumber(...)
    findByIsbn(...)
    ...


No business logic. Zero.

What DAO must NOT do
“check if book available”
“reduce copies”
“create borrow record logic”

👉 That is NOT database work.

DAO methods take entities or primitive identifiers, NOT business context
DAO returns data, not decisions

### Design DAO Methods

1. BorrowBook



### Rules

Who manages the transaction?
    DAO or Service?

Correct answer: Service (we’ll enforce this).
    DAO should NOT open/close transactions.

