### Intro

AOP (Aspect-Oriented Programming) in Spring is a technique 
used to modularize cross-cutting concerns
( means it appers in many other functions in diffrent regions )
such as logging, security, and transactions,Exception handling and Monitoring Logic
by separating them from business logic.

Spring AOP works using runtime proxies 
that intercept method calls and execute additional behavior 
before, after, or around the target method execution.

Instead of writing the same code repeatedly in multiple classes, AOP allows you to define it once and apply it wherever needed.

AOP calls it autmiatically

#### Exception Handeling : No

The aspect catches the exception.

Now what should it return?

Book?
ResponseEntity<?>?
ApiResponse<Book>?
null?

The aspect doesn't know. It works with methods that may return many different types.

This is why AOP is a poor fit for converting exceptions into HTTP responses.


### AOP exception handling actually used for?

✅ Logging exceptions
✅ Sending alerts (Slack, email)
✅ Recording metrics
✅ Auditing failures
✅ Cleaning up resources
✅ Retrying operations (combined with retry mechanisms)\


### Difference between MiddleWare and AOP

| Middleware                                      |
| ----------------------------------------------- |
| Intercepts **HTTP requests/responses**          |
| Works at the web layer                          |
| Examples: authentication, CORS, request logging |


| Spring AOP                                                     |
| -------------------------------------------------------------- |
| Intercepts **method calls**                                    |
| Works anywhere in your application                             |
| Examples: transactions, method logging, performance monitoring |


### Middleware

Imagine a request comes into your application:

Client
   |
   v
Authentication Middleware
   |
Logging Middleware
   |
Controller

Each middleware processes the HTTP request before it reaches your controller.


### Spring AOP

Now imagine your controller calls a service:

Controller
     |
     v
Logging Aspect
     |
Transaction Aspect
     |
BankService.transferMoney()

The method itself is intercepted before it executes.


Spring AOP is similar to middleware because both intercept execution to perform additional logic. The key difference is that middleware intercepts HTTP requests and responses, while Spring AOP intercepts method executions inside Spring-managed beans.