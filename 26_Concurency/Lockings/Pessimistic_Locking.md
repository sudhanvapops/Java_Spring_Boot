### Pessimistic Locking

Someone else will interfer with the data
so aquire lock and chenges
so that other wont

so it will aquire the lock for modifying/locking fro another row while i am working with it

in psql its Select ... FOR UPDATE

The for update aquires the row lock

in spring
if findById
use findByIdWithLock(@Param("id) Long id)

Or else 

in repo
commonly used

@Lock()

@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Book> findById(Long id);



### WHich is Better

Optimistic locking isn't inherently better or worse. 
It is useful when conflicts are relatively uncommon. 
Pessimistic locking is useful when contention is expected and waiting is acceptable. 
Atomic SQL is another option when the business operation can be expressed as one conditional database operation.