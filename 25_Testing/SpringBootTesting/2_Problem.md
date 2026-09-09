### Problem

The problem with the database

if my application uses PostgreSql

my test could use PostgreSQL in my Laptop which is installed

now tests depend on your laptop's database.

Maybe:
    PostgreSQL isn't running
    wrong database version
    wrong password
    someone changed the database
    database contains old data

This is where Testcontainers comes in


### @TestContainers

Testcontainers allows your tests to run dependencies inside Docker containers.


For example:

Your test
    ↓
Spring Boot
    ↓
PostgreSQL container
    ↓
Docker


Instead of saying:
    "Use whatever PostgreSQL I have installed."

you say:
    "Start a PostgreSQL container specifically for my tests."



### @Container

@Testcontainers enables Testcontainers and JUnit integration.
@Container identifies the actual container.


Example:

``` java
@SpringBootTest
@TestContainers
class BookRepoTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");
    // builds a postgres container V16 and runs it

    @Test
    void shouldSaveBook() {
        // test
    }
    
}
```

TestContainer: 
    Testcontainers, manage the containers used by this test.
    During this JUnit test class, handle the lifecycle of the containers marked with @Container
    
Container: This is the container I want you to manage


### @ServiceConnection

Without @ServiceConnection, you would have to tell Spring:
    the PostgreSQL container is running on 
    this host and this port.
    Use these credentials and this JDBC URL

With @ServiceConnection:
    just add it on top of a container


pring Boot sees:

@ServiceConnection
        ↓
PostgreSQLContainer
        ↓
"Ah, this is a PostgreSQL service"
        ↓
Configure datasource automatically
        ↓
Spring DataSource
        ↓
PostgreSQL container