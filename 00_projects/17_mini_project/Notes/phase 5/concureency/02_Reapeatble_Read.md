### Repetable Read

Repeatable Read is the next isolation level above Read Committed.

READ COMMITTED: Each statement can see a newer committed value.

REPEATABLE READ: Once your transaction starts reading data, you keep seeing the same snapshot throughout that transaction.


A read 5
B makes it 4 and commits
A read it again it gives 5


Initial value = 5

Transaction A              Transaction B

SELECT → 5

                           UPDATE → 4
                           COMMIT

SELECT again
READ COMMITTED → 4
REPEATABLE READ → 5


PostgreSQL's REPEATABLE READ is actually stronger than the basic SQL definition suggests: it prevents phantom reads too, using its MVCC snapshot system.


But

Under PostgreSQL's Repeatable Read, A's transaction gets aborted with a serialization/concurrency error rather than silently overwriting B's change.

means 
A sees 5
B changes it to 4 commits
A changes it again
gets error and rollback 
and retry (not automatcially)


### Benift

With Repeatable Read, all those reads correspond to a consistent point-in-time view.

like when u are working in a same transaction
u dont want data to be modifed 

want to work with consistent data

