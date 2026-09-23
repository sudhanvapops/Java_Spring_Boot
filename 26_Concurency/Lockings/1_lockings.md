### Lockings in Database

- Pessimistic Locking
- Optimistic Locking


### Why we need it 

- like example 
take irctc when too many requests comes for the sme thing
Things like
race condition
lost updates 
dirty read
phantom read
non repetable read
deadlock
write skew
happen

concurrency control techniques as solutions to different database problems.

| Problem                           | What happens                                                                  | Typical solution                                       |
| --------------------------------- | ----------------------------------------------------------------------------- | ------------------------------------------------------ |
| **Race condition**                | Result depends on timing/order of concurrent transactions                     | Locking, atomic operations, proper isolation           |
| **Lost update**                   | One transaction overwrites another transaction's update                       | Optimistic locking, pessimistic locking, atomic update |
| **Dirty read**                    | Transaction reads uncommitted data from another transaction                   | `READ COMMITTED` or higher                             |
| **Non-repeatable read**           | Same query gives different result within one transaction                      | `REPEATABLE READ` or higher                            |
| **Phantom read**                  | Re-running a query returns new/different rows                                 | `REPEATABLE READ` / `SERIALIZABLE`                     |
| **Write skew**                    | Two transactions make individually valid changes that together violate a rule | Stronger isolation / locking                           |
| **Deadlock**                      | Transactions wait for each other forever                                      | Lock ordering, timeout, retry                          |
| **Double spending / overbooking** | Same limited resource gets allocated multiple times                           | Locking, atomic conditional update, constraints        |



### Pessimistic locking

problem it addresses
    Concurrent conflicting access
    Lost updates
    Race conditions
    Over-allocation

Solution:
    LOCK → READ → MODIFY → COMMIT


Here in LMS

u are solving  all

over allocation
borrow one book 10 successfull transaction


### Optimistic locking

Problem it primarily addresses:
    Lost updates
    Concurrent modifications

Strategy:
    READ → WORK → CHECK VERSION → UPDATE


### Atomic conditional update

Problem:
    Race condition
    Over-allocation


### Isolation levels

Dirty read
Non-repeatable read
Phantom read

