### CountDown Latch

- A CountDownLatch is like a gate/barrier that waits for a certain number of events to happen.

- A syncronization Aid
that allows one or more threads to wait until a set of operations being peroformed in other threads are completed


latch.await() -> awaits for all the threads to finish
latch.countdown() -> countdown the latch