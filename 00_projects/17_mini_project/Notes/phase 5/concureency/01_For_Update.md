### FOr Update


Without it:
    S1 reads 1
    S2 reads 1
    S1 borrows
    S2 thinks it can borrow


With For Update:
    S1 reads 1 + locks row
    S2 cannot read/lock it yet
    S1 borrows → 0
    S1 commits
    S2 gets lock
    S2 reads 0
    S2 rejects borrow


FOR UPDATE is used when you want to lock the rows you selected 
so that another transaction cannot modify those rows until your transaction finishes.

even read lock
even reading cannot be done when another transaction acquired the lock
under Trnasaction

and if both wants lock

### Read Commited 

READ COMMITTED is the default transaction isolation level in PostgreSQL.
Means:
    A transaction can only read data 
    that has already been committed by another transaction.

READ COMMITTED gives you a fresh snapshot for each SQL statement, not necessarily for the entire transaction.

FOR UPDATE under READ COMMITTED has specific behavior where a waiting statement can re-check the updated row after the other transaction commits.

If Transaction A changes something but hasn't committed, Transaction B doesn't see A's uncommitted value.

