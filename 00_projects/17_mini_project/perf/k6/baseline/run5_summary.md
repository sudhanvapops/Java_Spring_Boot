### Summary run 5

latency rises sharply as concurrency increases


Test 1 and Test 2

doubling the configured concurrency nearly doubled throughput, but p95 latency more than doubled. The system handled more requests per second, but the slowest 5% of requests experienced increasingly long delays.

First Done with: 20 VUS
Second Done with: 40 VUS
Third Done with: 40 VUS and all three endpoints with same starting

### stats 

Measurement	First report section	Second report section	Third report section
Peak VUs (vus_max)	40	80	123
Throughput (http_reqs)	32.0 req/s	60.6 req/s	108.0 req/s
Overall average latency	51.5 ms	77.5 ms	591.0 ms
Overall p95 latency	105.4 ms	227.6 ms	1,028.5 ms
Overall p99 latency	161.8 ms	384.7 ms	1,426.3 ms
Overall maximum latency	1,184.6 ms	566.0 ms	2,152.9 ms

