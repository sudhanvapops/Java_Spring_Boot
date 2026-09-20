### Why Concurency

if two people borrowed book in a transaction
transaction can set boundries to wait for other to finish



but if two request read 1 book
and both reduced available copy
available copy  = -1
That is the race


so we have to put concurency to avoid these situation
when two or more request comes you have to 
aquire lock and execute


### Concepts

READ COMMITTED vs REPEATABLE READ
row locks
lost updates
SELECT ... FOR UPDATE
serialization failure
