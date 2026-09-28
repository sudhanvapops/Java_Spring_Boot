B3 Chapters


Chapter 1 — Transaction Boundary
    Topics
        What exactly is a transaction boundary?
        Where does the transaction start?
        Where does it end?
        What @Transactional actually does
        Commit vs rollback
        How this applies to your borrowBook()

You can answer:
    "What exactly is protected by my @Transactional?"
    and trace the transaction from method entry → database work → commit/rollback.



Chapter 2 — Persistence Context
      Topics
            What is the Persistence Context?
            How Hibernate keeps track of entities
            Managed entity
            Why findById() can give you a managed Book
            Transaction ↔ Persistence Context relationship
            You earn

You stop thinking:
      "I fetched a Java object from the database."

and start thinking:
      "Hibernate fetched the Book and is now managing its state."



Chapter 3 — Entity Lifecycle & Dirty Checking
      Topics
            Entity states
            Transient
            Managed
            Detached
            What "managed" actually means
            Dirty checking

Why this works:
      @Transactional
      public void test() {
      Book book = repository.findById(1L).orElseThrow();

      book.setTitle("New Title");

      // no save()
      }

You understand why Hibernate can generate:
      UPDATE book
      SET title = 'New Title'
      WHERE id = 1;

even though you never called:
      bookRepository.save(book);

This is one of the main things B3 is trying to teach you.


Chapter 4 — Flush vs Commit
Topics
What is flush()?
When Hibernate sends SQL
Flush vs commit
Why SQL doesn't necessarily execute at the exact line where you modify the entity
Basic flush timing
You earn

You can distinguish:

Java object changed
        ↓
Hibernate knows about change
        ↓
FLUSH
        ↓
SQL reaches database
        ↓
COMMIT
        ↓
transaction becomes permanent

So you won't confuse:

"Hibernate generated the UPDATE"

with:

"The transaction committed."

Chapter 5 — Transaction Effects in Your LMS
Topics

Apply everything to your actual:

borrowBook()

Understand:

@Transactional
      ↓
find Book
      ↓
Book becomes managed
      ↓
check available copies
      ↓
modify Book
      ↓
create BorrowRecord
      ↓
dirty checking
      ↓
flush
      ↓
SQL UPDATE + INSERT
      ↓
commit
You earn

You can explain the complete lifecycle of one borrow operation, instead of seeing Spring Data repository calls as isolated operations.

Chapter 6 — Spring Proxying
Topics
How Spring implements @Transactional
Proxy around your service
Why this:
Controller
   ↓
Spring Proxy
   ↓
@Transactional method

works.

Why this causes a problem:
this.someTransactionalMethod();
You earn

You understand why @Transactional sometimes appears to "not work."

Chapter 7 — Self-Invocation
Topics
Calling a @Transactional method from the same class
Why self-invocation bypasses the Spring proxy
What actually happens
How to fix/structure it

The B3 source specifically asks you to break this and prove what happens.

You earn

You can answer:

"Why doesn't @Transactional work when I call the method using this.method()?"

without memorizing a rule.

Chapter 8 — Transaction Propagation
Topics
REQUIRED
REQUIRES_NEW
Existing transaction
Joining an existing transaction
Starting an independent transaction
Suspending the outer transaction
Main example

Audit logging:

Outer transaction
      ↓
borrowBook()
      ↓
something fails
      ↓
ROLLBACK

but:

Audit transaction
      ↓
REQUIRES_NEW
      ↓
COMMIT

so the audit record survives the outer rollback.

You earn

You understand why multiple transaction boundaries can exist inside one call chain.

Chapter 9 — LazyInitializationException
Topics
Lazy loading
Persistence context closing
Accessing a lazy collection after the transaction
Why LazyInitializationException happens
Fixing it in different ways

The source specifically asks you to reproduce this by returning an entity with a lazy collection and touching it in the controller.

You earn

Instead of seeing:

LazyInitializationException

as some mysterious Hibernate error, you'll understand:

Controller
   ↓
Entity returned
   ↓
Transaction already ended
   ↓
Persistence context closed
   ↓
lazy collection requested
   ↓
Hibernate cannot load it
   ↓
LazyInitializationException
Final B3 Skill

At the end, the real thing you're supposed to gain is this:

                    Spring
                      │
                 @Transactional
                      ↓
              Transaction Boundary
                      │
                      ↓
              Persistence Context
                      │
                ┌─────┴─────┐
                ↓           ↓
             Managed     Entity State
             Entity
                │
                ↓
          Dirty Checking
                │
                ↓
              Flush
                │
                ↓
             SQL
                │
                ↓
          Commit/Rollback

And then one level deeper:

Spring @Transactional
        ↓
Spring Proxy
        ↓
Transaction
        ↓
Hibernate Persistence Context
        ↓
Managed Entity
        ↓
Dirty Checking
        ↓
Flush
        ↓
SQL
        ↓
Commit

That is the core mental model B3 is building.