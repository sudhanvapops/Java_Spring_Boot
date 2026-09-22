### Intro

- conureny can be achived by two ways
- threads
- process

- process are instance of a program which are run independent from each other
- inside each process we can use threads to execute code concurently


### Runnable 

- use runnable for a thread

- make a runnable task
- give that task to threads to execute


but this is error prone in large apps
like race condition, lost update and more


so Concurrency Api makes it simple

returns Void

### Callable 

like runnable it will return a value
think this as a async operation in js

returns Value: Future with a result


### Future

In Callable:
    Give me the result later

Runnable doesn't return a value, so the Future is basically used to:
    wait for completion
    check if finished
    cancel the task
    detect exception

| Method               | What it does                                 |
| -------------------- | -------------------------------------------- |
| `get()`              | Waits until task finishes, then gives result |
| `get(timeout, unit)` | Waits only for specified time                |
| `isDone()`           | Checks whether task has finished             |
| `isCancelled()`      | Checks whether task was cancelled            |
| `cancel(true)`       | Tries to cancel the task                     |


Can throw two checked exceptions:
    InterruptedException → the thread waiting on get() was interrupted.
    ExecutionException → the task itself threw an exception.

### Concurrency Api

- They run Asyncronus tasks
- manages a pool of threads

### ExecutorService

- manager of worker threads

- all beloning to the Executor pool will be reused by the tasks
- Exceutors should be stopped explicitly
- or they are lisiting for new task indefinetly

To Stop

shutdown() waits for currently running taks to finish
shutdownNow() intrupts all running taskas and shuts donw the executor


answers who runs my tasks?

### submit

executor.submit(callable)
callable methid want to run in a thread
submit will submit the request to execute


