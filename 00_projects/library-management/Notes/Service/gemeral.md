### Two Approaches

for borrowBook()
uses id

❌ Database-driven

Problems:
leaks internal DB design
caller must know IDs
not meaningful in domain

✅ Domain-driven (Your approach)
uses cardNumber

Stable identity
Decouples layers
Service layer doesn’t depend on DB internals
DAO handles ID resolution


