### Concepts


1. Aspect

first make it a componet

@Component
@Aspect

An Aspect is simply:
    A class containing extra work.


LoggingAspect
TransactionAspect
SecurityAspect


Aspect = Helper


2. Join Point

A place where Spring could do something.
    borrowbook()
    returnbook()

"Possible" interception poins


public void logMethodCall(JoinPoint jp){
    LOGGER.info("Method Called: "+jp.getSignature().getName());
}

3. Pointcut 

like a filter

Suppose you have 500 methods.

Do you want logging everywhere?
No.

Maybe only:
    Service package
    or
    All methods starting with save*


A Pointcut says:
    "Intercept THESE methods."


execution(* com.library.service.*.*(..))
Meaning:
    Every method inside the service package.



4. Advice

Advice is:
    What should happen?


Before:
    Log


After:
    Clean cache

After Exception:
    Log Error

After returning:
    do something


Around: (total method start and end)
    Measure Time

@"advicename":after, before

Eg:
@Before("expression")

// return type, fully classified class name, method name, args
// * all return type, * all the class name, *(..) all the methods (..) means all arguments

this is point cut

@Before("execution(* *.*(..))")
    @Before("execution(* com.sudhanva.server2.service.ProductService.*(..))")


5. Weaving

How do Aspects connect to methods?
Spring secretly creates a "proxy".


Your Code
     ↓
Proxy
     ↓
Aspect
     ↓
Real Method

this connection process is called Weaving (done at runtime, u can also in compile time)


6. Target Object is the joinpoint's object
which is going to be wraped up with proxy object


### Putting It Together

Suppose you want logging.

Aspect
    LoggingAspect

Pointcut
    All Service methods

Advice
    @Before

Join Point
    borrowBook()
    returnBook()
    addBook()

Spring combines them automatically.



### One line

| Concept        | Remember it as                                |
| -------------- | --------------------------------------------- |
| **Aspect**     | Helper that performs extra work               |
| **Join Point** | A method that can be intercepted              |
| **Pointcut**   | Which methods to intercept                    |
| **Advice**     | What action to perform                        |
| **Weaving**    | Spring connecting everything together         |
| **Proxy**      | The middleman through which method calls pass |
