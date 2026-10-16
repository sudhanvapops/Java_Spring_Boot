### Why Load Testing Matters

When 1 request complets quiclkly everything appers good
nothing appers wrong


But When 50 concurrent Users Requests come together
- Req's compete for database connection
- cpu and other resources get shared
- response time may rise
- throughput may plateau
- errors may apperas


So The Key Idea is System works correctly under light traffic 
may performe poorly under concurrent trafiic

testing play critical role in evaluating performance of application under load 

so its a latency matrix 

### What we will messure


Metric	        What it tells us
Throughput	    How many requests the system handles per second
Latency	        How long a request takes
p50	            Half of requests finish within this time
p95	            95% of requests finish within this time
p99	            99% of requests finish within this time
Error rate	    What proportion of requests fail



### p50 p90 p95 p99

monitoring and ooptimising p95 can make better ux for majority of users
p90 helps us undertsand most of the users leaving ut top 10% users
so its for majority of users 
p99 worst case performance


P50	
50% of requests finish within this time.	
Measures typical performance and establishes a baseline.


P90	
90% of requests finish within this time; 10% take longer.
Understands performance for the slower 10% of requests.


P95	
95% of requests finish within this time; 5% take longer.	
Helps optimise the experience for most users while catching significant slowdowns.


P99	
99% of requests finish within this time; 1% take longer.
Exposes tail latency, which can reveal serious bottlenecks under load.


P50 tells you what's typical, 
P95 tells you whether the slower tail is becoming problematic,
P99 helps expose extreme latency. 
P90 is also useful but P95 is often a more sensitive operational target for user-facing APIs.