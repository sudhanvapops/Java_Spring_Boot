### Race 

Initial: available_copies = 1

S1: BEGIN
S2: BEGIN

S1: SELECT → 1
S2: SELECT → 1
       ↓
Both independently conclude:
"Yes, a copy is available."

S1: UPDATE → available_copies - 1
S2: UPDATE → waits for S1
       ↓
S1 commits
       ↓
S2 continues its UPDATE
       ↓
Final: available_copies = -1


PostgreSQL did protect the physical row update.
S2 didn't simultaneously modify the row. It waited.
But PostgreSQL couldn't know that your business rule was:
"You may decrement only if a copy was available when you made the decision."

PostgreSQL transactions prevent race conditions" is wrong.



### SQL

UPDATE books
SET available_copies = available_copies - 1
WHERE id = 1;

Means whenevr my trn comes excute the above statememt
it doesnt say if busniess rule gt voilated dont do it

So after S1 commited S2 also went to decrement copy


### Conclsuion

The database protected the UPDATE from happening simultaneously.
It did NOT protect the business decision made from the earlier READ.


