### Intro To Junit

1. What is unit testing 

- testing a part of code
- or a single class


2. Integration testing 

- how diffrent modules behave with each other
- tetsing that
- for example in lms

- when i say borrow this book
- first db is hit and tets the authenticity of book and memeber
- check available
- then go to Book Module change available copy
- Go to Borrow module add this person took this amny book
- Add in transaction record 

so diffrernt modules are wotking together here



### Naming Convention

Typical JUnit naming
    BorrowClass.java
    BorrowClassTest.java


BorrowClassTest is a convention, not a JUnit requirement.


### Package Structre

- it will be in the same pacakge as of main testing class


### Testing

in Junit5 methods should be public 
and should not return anything


@Test is a JUnit annotation that tells JUnit:
    This method is a test that should be executed.

and a function should test single result


@Test → tells JUnit that shouldBorrowBook() is a test method.
shouldBorrowBook() → contains the actual test.
When you run the tests, JUnit finds methods marked with @Test and executes them.



### Assert 

- Think of a test as having two parts:
    1. Do something
    2. Check whether the result is what we expected

and by using assertion we are doing the 2nd one 


Example: 

@Test
void additionTest() {
    int result = 2 + 3;

    assertEquals(5, result);
}

JUnit, I expect result to be 5. If it isn't, fail this test.


### Type of Assertions

assertEquals()
assertNotEquals()
assertTrue()
assertFalse()
assertNull(): check something is null
assertNotNull()


### Test Cycle

Basic Test Cycle

sometimes all of the method inside a test class
before running we need somkind of setup which is same 


Some time We need to cleanup resouces or do after every methd run 

that time we use 

@BeforeEach
@AfterEach

theya are called before and after each test case inside class

Example:

testing a db
after each test
db tables should be droped
sp db can start fresh



### Static vs Instance Test Setup

instance setup

each function gets its own
before and after 

@BeforeEach
@AfterEach

Static/class-level setup

now before and after all
runs once for the entire test class.


@BeforeAll
@AfterAll

@BeforeAll
static void setup(){

}

Why static?
    By default, 
    JUnit creates a new instance of the test class 
    for each test method.

because static belongs to the class, rather than a particular object.


Example:

static: to create test container
normal for every instance Data seeding


@BeforeEach is very commonly used for test state
@BeforeAll is better suited to expensive resources that can safely be shared.

### Tips

example if i have a methods that returns

< 60 : F
> 60 : B
> 80 : A
> 90 : S

Then all of them should be tested sepratly 
one function one test

