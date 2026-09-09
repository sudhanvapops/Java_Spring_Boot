### Intro

Junit can run tests 
but junit cannot start spring

So Spring Boot testing gives you tools 
to test your application 
with Spring's dependency injection 
and configuration involved.



### Example:

@SpringBootTest
class BookServiceTest {

    @Autowired
    BookService bookService;

    @Test
    void shouldFindBook() {
        Book book = bookService.findById(1L);

        assertEquals("Clean Code", book.getTitle());
    }
}

no longer testing just a Java object.
testing something much closer to:

My test
   ↓
Spring
   ↓
BookService
   ↓
BookRepository
   ↓
Database


Spring Boot testing: 
    provides tools and infrastructure 
    for testing Spring Boot applications 
    in a controlled test environment.



### spring-boot-starter-test

Maven dependency that gives 
common testing toolkit for Spring Boot.

It brings together commonly used testing libraries, including:
    JUnit 5 → writing/running tests
    Spring Test → testing Spring applications
    AssertJ → readable assertions
    Mockito → mocking dependencies
    other supporting test libraries

You choose whether a particular test actually uses Spring.


Example:

@Test
normal Junit test

@SpringBootTest
uses spring



### Application Context


When i write
@Autowired
BookRepo bookRepo

i didnt do new BookRepo()

Spring autmactically does it 
creates bean


The object responsible for 
managing these Spring objects 
is ApplicationContext.

Think of it as Spring's container/registry of application objects (beans).


Spring:
    Creates objects
    Configures them
    Connects their dependencies
    Manages their lifecycle

Those managed objects are called beans.


### Spring Test Context
Spring creates an ApplicationContext specifically for your test.

