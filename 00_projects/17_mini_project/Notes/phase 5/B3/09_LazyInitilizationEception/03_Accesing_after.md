### Accessing The Lazy Colletion After Transaction


Will give you LazyInitializationException


Initialization means:

Turning the not-yet-loaded lazy association into the actual collection of database-backed entities.



### Fixing

1. Do it inside the same transaction
initialize the object inside the transaction
and then when the object is accessed outside the transaction boudnry
it doenst cause teh problem


2. instead of loading again
use join fetch

When you fetch this Book, fetch this relationship as part of the query.


3. Instead of Using Book to fecth
design a DTO 

The service/repository fetches exactly the required data and creates the DTO.


4. keep the transaction open in the controler

But it is generally not the design we want to rely on merely to make lazy loading work.
Why?
Because now the web/controller layer is participating in the database transaction and persistence lifecycle.

Transaction boundaries should represent meaningful units of work.


5. Danger Fix EAGER


###

While the entity is managed, Hibernate can say:

I know what this object represents,
I know its relationships, 
and I still have a Session through which I can fetch missing data


Hibernate makes lazy loading possible 
by giving the entity/relationship Hibernate-managed state 
that can trigger database loading while the entity is associated with an active persistence context/session.


### Session

Session is basically the object that represents 
Hibernate's ongoing interaction with the database.


The Session:
    1. Talks to the database.
    2. Creates/manages the Book entity.
    3. Keeps track of that entity.
    4. Maintains the Persistence Context.
    5. Handles lazy loading.
    6. Detects changes to managed objects.
    7. Eventually synchronizes those changes with the DB.


Session
│
├── Persistence Context
│      │
│      ├── Book #1 → managed
│      ├── Book #2 → managed
│      └── User #5 → managed
│
├── DB connection / JDBC interaction
├── Transaction interaction
└── Hibernate operations


The Persistence Context is the collection/environment where managed entities live.

The Session is the Hibernate object that provides that environment and communicates with the database.