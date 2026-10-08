### Lazy Loading

Supose books has
@OneTpMany(fetch = FetchType.LAZY)
List<BorrowTransactions> borrowTransactions

LAZY means Hibernate does not load the related collection immediately

when i do book.fetch(id)

it will only fetch book
when if asked for its transaction
then only it will fetch the borrowtransactionrecords too



Why Hibernate do this

maybe that book has 10000 records
then if no body uses it
its a wasteage of memeory
and time consuming

if every time we fecth a book



### How Hibernate Load it later

When Hibernate gives you a lazy collection, 
it doesn't simply give you an ordinary fully-populated Java List.

Conceptually

Book
 │
 └── borrowTransactions
          │
          ↓
     Hibernate-managed
     lazy collection
          │
          ↓
     "Load me when accessed"

when asked
It then uses the Persistence Context / Hibernate Session and database connection to fetch the records.


For Hibernate to load that lazy collection, 
Hibernate needs to still have the machinery that manages the entity.

Lazy loading works naturally while the entity is still managed and the persistence context is available.


