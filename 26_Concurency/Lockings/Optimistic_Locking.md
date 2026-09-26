# Optimistic Locking

Optimistic locking assumes that concurrent conflicts are uncommon, 
so transactions do not lock rows while reading them.

Instead, each row has a version number.
When a transaction updates the row, the database checks that the version is still the same:

UPDATE book
SET available_copy = 0, version = 6
WHERE id = 1 AND version = 5;

If another transaction already changed the row, the version will no longer match and the update affects 0 rows. Hibernate detects this and throws an `OptimisticLockException`.

### Trade-off

- Pessimistic locking → prevent conflicts by making transactions wait.
- Optimistic locking → allow concurrent work and detect conflicts when updating.

Optimistic locking is useful when conflicts are relatively rare and retrying/failing a conflicting transaction is acceptable.

In Spring/JPA, optimistic locking is commonly implemented using `@Version`.