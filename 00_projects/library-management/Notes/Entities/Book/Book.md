### Designing the Book entity

Should Represent: A type of book in the library (not individual copies)

Here is my book represents
Book information

1. Which Contains:
    - Book Id
    - ISBN 
    - Book Name
    - Author
    - Total Copies
    - No of Copies available

    Related With 
    BorrowRecord Table

2. Book:
    id (PK, generated)
    name (not null)
    isbn (unique, important, not null)
    authors (Set, many-to-many)
    totalCopies
    availableCopies

Justify each field (why it exists)
Book Id for Primary Key 
Book Name For the title of the book
Book Author Who written it since there can be multiple autors
No of copies available 

### Book ID (pk)
Book Id is Generated ID from database
If use Generated ID then Store ISBN
to identify editon and diffrensiate two books

#### ISBN
ISBN (unique, non-null ideally)
A very old book has no ISBN?
nullable but unique when present

### Book Name
- Non Nullable
Can two books have same name?
Yes but with diffrent author 


### author
If multiple authors exist, can a single field hold them?
So Use Set
“Book has a collection of Authors”

### noOfCopiesAvailable
if deived value then unecesarry db calls and calculations
if stored value its good


