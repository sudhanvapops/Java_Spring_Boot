### Intro 

JUnit → Testcontainers → Spring Boot → Flyway → PostgreSQL



### Junit 

For A1, learn these JUnit concepts:

- @Test
Marks a method as a test.

- Test class structure

class BookIntegrationTest {

    @Test
    void shouldSaveBook() {
        // test
    }
}

- Assertions

assertEquals(expected, actual);
assertTrue(condition);
assertNotNull(value);


- Basic test lifecycle
@BeforeEach
@AfterEach

You don't need to master these yet, but understand what they do.

- Static vs instance test setup
This becomes important in your A2 Testcontainers session.


### Spring Boot testing

- @SpringBootTest
- @Testcontainers
- @Containers
- @SerivceConnection


### Last Integrate Them

Flyway
Integration testing

