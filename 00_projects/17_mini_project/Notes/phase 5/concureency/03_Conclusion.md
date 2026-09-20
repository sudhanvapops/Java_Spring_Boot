### Conclusion


How to make a concurrent Transaction

1. Pessimistic locking — FOR UPDATE
    Lock the row before making the decision → other transaction waits while acquring the same lock.

2. Higher isolation — REPEATABLE READ
    Conflicting stale transaction gets aborted → application must handle/retry.

3. Atomic conditional UPDATE
    Make the invariant part of the UPDATE → check affected rows.

### Expanded

1. SELECT ... FOR UPDATE

Acquire a row lock → another transaction waits.

Tradeoff: transactions can block each other, so under high contention you can get more waiting/latency.


2. REPEATABLE READ

If another transaction changes the row you depended on, 
PostgreSQL can abort your transaction with a serialization error.

Tradeoff: the failed operation needs to be retried or reported as a failure. 
You don't necessarily need "another request" from the user; the application can retry the transaction itself.


3. With Condition

UPDATE book
SET available_copy = available_copy - 1
WHERE id = 1
  AND available_copy > 0;

But here the read and the condition are part of the same UPDATE statement
Under READ COMMITTED, PostgreSQL handles the concurrent update like this


Both Transaction reads 1

A 1 -> 0
commits

B checks again since its a part of the same transaction
and has Read Commited (same transaction can get diffrent values of commited data)

Now B 0 -> -1 X
Updates 0 rows


trade off: 
    It's great when the operation can be expressed as one atomic SQL statement, but less convenient when the business operation involves multiple pieces of state or several decisions.

wont get a standered error so have to interpret
0 means no book availble

