### Pessimistic Locking

Someone else will interfer with the data
so aquire lock and chenges
so that other wont

in psql its Select ... FOR UPDATE

in spring
if findById
use findByIdWithLock(@Param("id) Long id)

Or else 
in repo

commonly used

@Lock()

@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Book> findById(Long id);

