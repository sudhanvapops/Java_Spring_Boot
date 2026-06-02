### Spring Data JPA

- Spring Data JPA is a part of the Spring ecosystem that simplifies database access in Java applications using the Java Persistence API (JPA).

Instead of writing a lot of SQL and boilerplate code, Spring Data JPA lets you define repository interfaces, and it automatically generates the implementation for common database operations.


Spring Data JPA is not an ORM. 
It is a layer built on top of JPA that makes it easier to use an ORM.


- internally spring data jpa uses hibernate


Your Java Objects
       ↓
Spring Data JPA
       ↓
JPA Specification
       ↓
Hibernate (ORM)
       ↓
SQL Database



Hibernate
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();

session.persist(book);

tx.commit();
session.close();
Spring Data JPA
bookRepository.save(book);

Spring handles the session and transaction management for you


So:

✅ Still write:

@Entity
@Id
@OneToMany
@ManyToOne
@ManyToMany
@JoinColumn
@JoinTable

❌ Usually don't write:

SessionFactory
Session
Transaction
DAO implementation classes with basic CRUD methods
Most SQL queries


### Spring Managed Things

What Spring manages in Spring Data JPA are things like:

Repository beans
EntityManager
Database connections
Transactions
Hibernate Session lifecycle

The real purpose of Spring Data JPA
       It simplifies the DAO/Repository layer.