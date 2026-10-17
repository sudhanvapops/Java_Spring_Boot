### Detected


BorrowRecordService: getAllRecords()

GET 
  → BorrowRecordController.getAllRecords()
  → BorrowRecordService.getAllRecords()
  → borrowRecordRepo.findAll()          ← Query 1: SELECT * FROM borrow_record (N rows)
  → response.stream().map(mapToBorrowTransactionItemResponse)
      → borrowRecord.getBorrowTransaction()   ← LAZY — 1 SELECT per record (N queries)
      → .getMember().getId()                  ← loaded with BorrowTransaction (EAGER)
      → borrowRecord.getBook().getName()      ← EAGER ManyToOne — safe



BorrowTransaction: how many queries, and why?
Member: how many, and does it depend on the transaction’s mapping?
Book: how many, and does it depend on the mapping?
Two records can point to the same transaction or book. What does that do to the count? (Think about what Hibernate keeps in memory during one request.)


Limited To 20 With Pagination

1 qury for user lookup by email the jwt filter one

since the offset was 20 it should fetch around 20 each but
if the same book is chaced while quering it could not fire a query
ans similar to others also

Total queries: 34
Category	Number of queries
User lookup by email	1
Borrow records — paginated query	1
Book fetches by ID	16
Borrow transaction + member fetches	15
Count borrow records	1
Total	34

fired when just called the api with pagination 20

for full data set: about 1 + 50k + 100k ≈ 150k Queries

