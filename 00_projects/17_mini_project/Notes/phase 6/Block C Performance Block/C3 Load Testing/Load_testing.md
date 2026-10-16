### Load Testing using k6

A load test lets us systematically increase traffic and observe how the system responds.

k6 — Load generator
Sends HTTP requests and measures responses

Spring Boot LMS
Receives requests and runs application logic

PostgreSQL
Executes queries using database connections


k6 records response times, request rates, and failures. 
We use those measurements to investigate bottlenecks.


k6 generates traffic; it doesn't optimize your application or automatically identify the root cause. We interpret its measurements alongside application and database evidence.


### k6

k6 is a load-testing tool that lets you write test scenarios in JavaScript 
and run them against an application over HTTP.

1. define:
    - What to request — for example, search for books.
    - How much traffic to generate — for example, 10 virtual users.
    - How long to run — for example, 30 seconds.
    - What to measure or validate — response time, status codes, and errors.


A virtual user is an independently executing worker in k6. 
Each VU runs your test function, sends its requests, and repeats the scenario.

but they might or may not be concurrent request's


### Types of Performence Testing

Load Testing
Stress Testing 
Spike testing 
Soak Testing

