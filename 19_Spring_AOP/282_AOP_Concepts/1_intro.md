### AOP Concepts

Imagine You Own a Library 🏛️

You have these employees:

Borrow Book
Return Book
Add Book
Delete Book


Each employee has their own job.

Now your manager says:
    "Before anyone starts working, write their name in a logbook."


So every employee now does this:
    log();
    borrowBook();


another rule:
    "Measure how long every task takes."
another rule:
    "Only admins can delete books."


    log();
    checkAdmin();
    startTimer();
    deleteBook();
    stopTimer();

Now every method is filled with things that aren't its actual job.

That's all it should care about.
    borrow_book()

    Not logging.
    Not security.
    Not timing.

### Spring Says...

"Let the employee do only their job."
We'll hire another person.

Logger
Whenever someone starts working,
write it in the log.

Security Guard
Before Delete Book,
check if the user is Admin.

Time Keeper
Start stopwatch before.
Stop after.

Now the employee only does:
    borrowBook();


Everything else happens automatically.
    That automatic behavior is AOP.

