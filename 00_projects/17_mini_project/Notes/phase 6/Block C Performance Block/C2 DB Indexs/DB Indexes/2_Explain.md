### Explain And Explain ANALYZE

EXPLAIN shows the plan the planner intends to use, with estimates. 
It doesn’t run the query.

EXPLAIN ANALYZE actually runs the query and adds the real numbers.


### The habit that matters: estimate vs actual

Compare rows= (estimated) with actual rows=. 
If they differ wildly (say estimated 10, actual 100,000), 
the planner is working from stale statistics and may pick a bad plan. 

The usual fix is ANALYZE tablename;


EXPLAIN is like a debugger for SQL performance. 
It tells you how the database intends to do the work
And EXPLAIN ANALYZE tells you what actually happened.



### Where it Fits

Slow endpoint 
- find the slow query (logs / Hibernate SQL output)
- EXPLAIN ANALYZE it
- see Seq Scan + big "Rows Removed by Filter"
- add/adjust index → EXPLAIN ANALYZE again to confirm