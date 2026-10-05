### Transaction Manager

proxy → intercepts your method call
Transaction Manager → actually manages the transaction


A Transaction Manager in Spring is the component 
that controls the transaction lifecycle.

it decides:

When to start a transaction
When to commit
When to rollback
How to handle transaction boundaries
Which transaction/resource (JPA, JDBC, etc.) is involved

Common One: Spring Boot + JPA -> JpaTransactionManager