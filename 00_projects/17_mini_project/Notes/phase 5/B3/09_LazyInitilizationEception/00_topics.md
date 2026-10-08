### Chapter 9 Topics

1. Lazy Loading
    - What LAZY actually means
    - Why Hibernate doesn't immediately load a collection
    - What happens when you access it while the entity is managed

2. Persistence Context Closing
    - How the persistence context is tied to the transaction
    - What happens when the transaction ends
    - Entity becoming detached

3. Accessing a Lazy Collection After the Transaction
    - Controller → Service → Repository flow
    - Why the entity can still exist in memory
    - But its lazy collection cannot necessarily be loaded

4. Why LazyInitializationException Happens
    - The complete chain:

        Controller
            ↓
        Entity
            ↓
        Transaction ended
            ↓
        Persistence Context closed
            ↓
        lazy collection accessed
            ↓
        Hibernate tries to load it
            ↓
        No active persistence context
            ↓
        LazyInitializationException


5. Fixing LazyInitializationException

    - We'll understand the different approaches and, importantly, when each one makes sense, rather than memorizing fixes.

