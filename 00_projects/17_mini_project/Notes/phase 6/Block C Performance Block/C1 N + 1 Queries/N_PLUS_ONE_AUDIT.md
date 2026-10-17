# N+1 Query Audit — library-management-v2

> Produced via Graphify knowledge-graph traversal + direct source inspection.  
> **No files were modified.** All findings are read-only analysis.

---

## Summary

| # | Severity | Location | Status |
|---|----------|----------|--------|
| 1 | 🔴 HIGH | `BorrowRecordService.getAllRecords()` | **Confirmed** |
| 2 | 🔴 HIGH | `BorrowRecordService.getAllMemberRecords()` | **Confirmed** |
| 3 | 🔴 HIGH | `BorrowTransactionService.getAllTransaction()` | **Confirmed** |
| 4 | 🔴 HIGH | `BorrowTransactionService.getAllTransactionByMemberId()` | **Confirmed** |
| 5 | 🟠 MED  | `BorrowTransactionService.getTransactionById()` | **Confirmed** |
| 6 | 🟠 MED  | `MemberService.deleteMember()` | **Confirmed** |
| 7 | 🟠 MED  | `BookService.deleteBook()` | **Confirmed** |
| 8 | 🟡 LOW  | `BorrowRecordService.getAllUnreturnedRecords()` | **Mitigated** (EntityGraph present) |
| 9 | ✅ CLEAR | `BorrowTransactionService.borrowBook()` loop | False positive — in-memory only |

---

## Entity Relationship Map (for context)

```
Member (1) ──OneToMany──> BorrowTransaction (N)
                              │
                         OneToMany──> BorrowRecord (N)
                                          │
                                    ManyToOne──> Book
                                    ManyToOne──> BorrowTransaction
```

**Default fetch strategy (no FetchType specified = JPA defaults):**
- `@OneToMany` → **LAZY** (confirmed: no `fetch=` on any `@OneToMany` in this codebase)
- `@ManyToOne` → **EAGER** (JPA default)

---

## Finding 1 — 🔴 HIGH: `BorrowRecordService.getAllRecords()`

**File:** `Service/BorrowRecordService.java` lines 84–99  
**Endpoint:** `GET /borrow-records` → `BorrowRecordController.getAllRecords()`

### Execution Path
```
GET /borrow-records
  → BorrowRecordController.getAllRecords()
  → BorrowRecordService.getAllRecords()
  → borrowRecordRepo.findAll()          ← Query 1: SELECT * FROM borrow_record (N rows)
  → response.stream().map(mapToBorrowTransactionItemResponse)
      → borrowRecord.getBorrowTransaction()   ← LAZY — 1 SELECT per record (N queries)
      → .getMember().getId()                  ← loaded with BorrowTransaction (EAGER)
      → borrowRecord.getBook().getName()      ← EAGER ManyToOne — safe
```

### The N+1 Loop
```java
// BorrowRecordService.java L97
response.stream().map(item -> mapToBorrowTransactionItemResponse(item)).toList();

// Inside mapToBorrowTransactionItemResponse (L65–L77):
borrowRecord.getBorrowTransaction().getMember().getId()   // L70 ← lazy load per record
borrowRecord.getBorrowTransaction().getMember().getName() // L71
borrowRecord.getBook().getName()                          // L72
```

`BorrowRecord.borrowTransaction` is `@ManyToOne` — JPA default is EAGER, BUT:  
`findAll()` on `BorrowRecordRepo` has **no `@EntityGraph`**, so Hibernate may not join-fetch it.  
The author already added `@EntityGraph` on `findByReturnDateIsNull()` (line 64) for the same mapper — proof this path is known to need it.

### Fix
```java
// BorrowRecordRepo.java — mirror the existing @EntityGraph on findByReturnDateIsNull
@EntityGraph(attributePaths = {"borrowTransaction", "borrowTransaction.member", "book"})
@Query("SELECT br FROM BorrowRecord br")
List<BorrowRecord> findAllWithGraph();
```
Call `findAllWithGraph()` instead of `findAll()` at line 88.

**Files to change:** `BorrowRecordRepo.java` (add method), `BorrowRecordService.java:88` (call it)

---

## Finding 2 — 🔴 HIGH: `BorrowRecordService.getAllMemberRecords()`

**File:** `Service/BorrowRecordService.java` lines 103–123  
**Endpoint:** `GET /borrow-records/member/{memberId}`

### The N+1 Loop
```java
// L112
List<BorrowRecord> response = borrowRecordRepo.findByBorrowTransactionMemberId(memberId);

// L121 — same mapper as Finding 1, same lazy-load path
response.stream().map(item -> mapToBorrowTransactionItemResponse(item)).toList();
```

`findByBorrowTransactionMemberId` is a derived query with **no `@EntityGraph`**.

### Fix
```java
// BorrowRecordRepo.java — add EntityGraph to existing method signature
@EntityGraph(attributePaths = {"borrowTransaction", "borrowTransaction.member", "book"})
List<BorrowRecord> findByBorrowTransactionMemberId(Long id);
```

**Files to change:** `BorrowRecordRepo.java:30`

---

## Finding 3 — 🔴 HIGH: `BorrowTransactionService.getAllTransaction()`

**File:** `Service/BorrowTransactionService.java` lines 129–150  
**Endpoint:** `GET /borrow-transactions`

### Execution Path
```
GET /borrow-transactions
  → BorrowTransactionService.getAllTransaction()
  → borrowTransactionRepo.findAll()    ← Query 1: SELECT * FROM borrow_transaction (N rows)
  → .stream().map(mapToBorrowTransactionResponse)
      → bt.getMember().getName()        ← EAGER ManyToOne — safe
      → bt.getRecords()                 ← LAZY OneToMany — 1 SELECT per transaction (N queries)
          → each record → borrowRecord.getBook() ← EAGER — safe
```

### The N+1 Loop
```java
// BorrowTransactionService.java L99 (inside mapToBorrowTransactionResponse)
.books(mapToBorrowTransactionItemResponse(bt.getRecords()))
//                                         ^^^^^^^^^^^^ LAZY — fires SELECT per transaction
```

`BorrowTransaction.records` is `@OneToMany(mappedBy="borrowTransaction", cascade=ALL)` → **LAZY** by default.  
`BorrowTransactionRepo.findAll()` has **no `@EntityGraph`**.

### Fix
```java
// BorrowTransactionRepo.java
@EntityGraph(attributePaths = {"member", "records", "records.book"})
@Query("SELECT bt FROM BorrowTransaction bt")
List<BorrowTransaction> findAllWithGraph();
```
Call `findAllWithGraph()` at line 132.

**Files to change:** `BorrowTransactionRepo.java` (add), `BorrowTransactionService.java:132` (call it)

---

## Finding 4 — 🔴 HIGH: `BorrowTransactionService.getAllTransactionByMemberId()`

**File:** `Service/BorrowTransactionService.java` lines 153–178  
**Endpoint:** `GET /borrow-transactions/member/{memberId}`

### The N+1 Loop
Same as Finding 3, scoped to one member:

```java
// L160
List<BorrowTransaction> list = borrowTransactionRepo.findByMemberId(memberId);

// L168–170 — bt.getRecords() lazy per transaction
responses = list.stream()
    .map(transaction -> mapToBorrowTransactionResponse(transaction))
    .toList();
```

### Fix
```java
// BorrowTransactionRepo.java — add @EntityGraph to existing derived method
@EntityGraph(attributePaths = {"member", "records", "records.book"})
List<BorrowTransaction> findByMemberId(Long memberId);
```

**Files to change:** `BorrowTransactionRepo.java:13`

---

## Finding 5 — 🟠 MED: `BorrowTransactionService.getTransactionById()`

**File:** `Service/BorrowTransactionService.java` lines 108–124  
**Endpoint:** `GET /borrow-transactions/{id}`

### The Pattern
```java
// L112 — fetches transaction but NOT its records
BorrowTransaction bt = borrowTransactionRepo.findById(id)...

// L116 — triggers lazy load of records
mapToBorrowTransactionResponse(bt)
  → bt.getRecords()   // 1 extra SELECT: SELECT * FROM borrow_record WHERE borrow_transaction_id=?
```

Not a classical N+1 (fixed number of extra queries = 1), but it's an avoidable round-trip on every single fetch.

### Fix
```java
// BorrowTransactionRepo.java
@EntityGraph(attributePaths = {"member", "records", "records.book"})
Optional<BorrowTransaction> findById(Long id);
```

**Files to change:** `BorrowTransactionRepo.java` (override `findById`)

---

## Finding 6 — 🟠 MED: `MemberService.deleteMember()` — lazy cascade traversal

**File:** `Service/MemberService.java` lines 135–161  
**Endpoint:** `DELETE /members/{id}`

### The N+1 Cascade
```java
// MemberService.java L147–150
boolean hasActiveBorrow = member.getBorrowTransactions()    // LAZY — 1 SELECT (T transactions)
    .stream()
    .flatMap(t -> t.getRecords().stream())                  // LAZY per transaction — T SELECTs
    .anyMatch(record -> record.getReturnDate() == null);
```

`Member.borrowTransactions` → LAZY → 1 SELECT for the list.  
Then each `transaction.getRecords()` → LAZY → **1 SELECT per transaction**.  
For a member with T transactions: **1 + T queries** to answer a simple yes/no question.

### Fix — use existing repo method (zero new code)
```java
// MemberService.java — replace lines 147–150 with:
boolean hasActiveBorrow = !borrowRecordRepo
    .findByBorrowTransactionMemberIdAndReturnDateIsNull(id)
    .isEmpty();
```

`BorrowRecordRepo.findByBorrowTransactionMemberIdAndReturnDateIsNull` already exists at line 19.  
This reduces 1+T queries to **1 query**.  
Inject `BorrowRecordRepo` into `MemberService`.

**Files to change:**
- `MemberService.java:24` — add `BorrowRecordRepo` field + constructor arg
- `MemberService.java:147–150` — swap traversal for repo call

---

## Finding 7 — 🟠 MED: `BookService.deleteBook()` — lazy collection for existence check

**File:** `Service/BookService.java` lines 257–283  
**Endpoint:** `DELETE /books/{id}`

### The Lazy Collection Access
```java
// BookService.java L269–271
boolean isCurrentlyBorrowed = book.getBorrowRecords()  // LAZY — 1 extra SELECT
        .stream()
        .anyMatch(record -> record.getReturnDate() == null);
```

`Book.borrowRecords` is `@OneToMany(mappedBy="book")` with no fetch type → **LAZY**.  
`bookRepo.findById(id)` does not join `borrowRecords`.  
Hibernate fires `SELECT * FROM borrow_record WHERE book_id=?` just to check one condition.

### Fix — use an EXISTS query
```java
// BorrowRecordRepo.java — add:
boolean existsByBookIdAndReturnDateIsNull(Long bookId);

// BookService.java L269 — replace with:
boolean isCurrentlyBorrowed = borrowRecordRepo.existsByBookIdAndReturnDateIsNull(id);
```

Zero collection loading. One `SELECT EXISTS(...)`.  
Inject `BorrowRecordRepo` into `BookService`.

**Files to change:**
- `BorrowRecordRepo.java` — add `existsByBookIdAndReturnDateIsNull`
- `BookService.java:28` — add `BorrowRecordRepo` field
- `BookService.java:269–271` — swap lazy check for repo call

---

## Finding 8 — 🟡 LOW: `getAllUnreturnedRecords()` — ALREADY MITIGATED

**File:** `Service/BorrowRecordService.java` lines 127–142  
**Endpoint:** `GET /borrow-records/unreturned`

```java
// BorrowRecordRepo.java L64–69 — EntityGraph correctly applied
@EntityGraph(attributePaths = {
    "borrowTransaction",
    "borrowTransaction.member",
    "book"
})
List<BorrowRecord> findByReturnDateIsNull();
```

✅ No N+1 here. **This is the template** for fixing Findings 1 and 2.

> ⚠️ Consistency gap: `findByReturnDateIsNull()` has `@EntityGraph` but `findAll()` and  
> `findByBorrowTransactionMemberId()` use the same mapper without it.

---

## Finding 9 — ✅ FALSE POSITIVE: `borrowBook()` loop

**File:** `Service/BorrowTransactionService.java` lines 262–308

```java
// L243 — one bulk SELECT for all books
List<Book> existingBooks = bookRepo.findAllById(bookIds);

// L262–308 — loop is purely in-memory
for (Book book : existingBooks) {
    book.getIsActive()          // field on loaded entity — no DB hit
    book.setAvailableCopy(...)  // dirty-marks entity, flushed in batch at commit
}
```

`bookRepo.findAllById` uses `@Query("SELECT b FROM Book b WHERE b.id IN :ids")` — one SELECT.  
The loop is in-memory. Hibernate dirty-checking handles the UPDATEs at commit.  
**No N+1.**

---

## What Was Already Done Well

| Good Practice | Where |
|---------------|-------|
| Bulk `findAllById` with `IN (?)` | `BookRepo.java:28` |
| JPQL DTO projection with explicit JOINs | `BorrowRecordRepo.java:40–61` (`findDueToday`) |
| `@EntityGraph` on a hot path | `BorrowRecordRepo.java:64–69` (`findByReturnDateIsNull`) |
| Flat repo query instead of lazy traversal | `BorrowTransactionService.java:199` — author explicitly noted the naive approach was avoided |

---

## Recommended Fix Order (smallest to largest effort)

1. **Finding 4** — 1 annotation on `BorrowTransactionRepo.findByMemberId`
2. **Finding 2** — 1 annotation on `BorrowRecordRepo.findByBorrowTransactionMemberId`
3. **Finding 6** — swap 4 lines in `MemberService.deleteMember`, inject existing repo
4. **Finding 7** — 1 new method in `BorrowRecordRepo`, swap 3 lines in `BookService`
5. **Finding 3** — new `@EntityGraph` query in `BorrowTransactionRepo`, update service call
6. **Finding 1** — same pattern as Finding 2, slightly different method
7. **Finding 5** — override `findById` with `@EntityGraph` in `BorrowTransactionRepo`

---

## Enable SQL Logging to Verify at Runtime

```yaml
# application.yml (temporarily)
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.orm.jdbc.bind: TRACE
```

Hit `GET /borrow-transactions` — count the `SELECT` statements. One per transaction beyond the first = confirmed N+1.

---

*Audit: 2026-10-15 | Graph: graphify-out/graph.json (766 nodes, 2560 edges) | Source: direct inspection of all service, repo, and entity files*

