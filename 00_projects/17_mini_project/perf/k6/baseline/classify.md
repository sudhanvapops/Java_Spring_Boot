Since PostgreSQL caching affected Run 2 onward, document the conditions under which each run was executed.
Run	Database state	How to use it
Run 1	Potentially cold cache	Cold-start observation
Run 2	Cache warmed	Warm-cache observation
Run 3	Cache warmed	Warm-cache repeatability check
Run 4	Cache warmed	Warm-cache repeatability check

Here is the structure I recommend:
Metric	Before optimization	After optimization	Improvement
SQL queries per request	To measure	To measure	Reduction %
Mean response time	To measure	To measure	Reduction %
p95 latency	To measure	To measure	Reduction %
p99 latency	To measure	To measure	Reduction %
Throughput (req/s)	To measure	To measure	Increase %
Error rate	To measure	To measure	Change

