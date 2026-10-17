# LMS Performance Test Report --- C3 Load Testing

## Objective

Measure the LMS search endpoints under load and observe how reducing the
HikariCP connection pool to **2 connections** affects latency,
throughput, and request failures.

## Test setup

-   Load tool: k6
-   Endpoints: author search, member records, member transaction records
-   Scenarios run sequentially: warm-up, then each endpoint gets its own
    30-second test window
-   Reduced-pool configuration:
    `spring.datasource.hikari.maximum-pool-size=2`
-   Reduced-pool runs:
    -   **Run 6:** 5 VUs per endpoint
    -   **Run 7:** 10 VUs per endpoint
-   During the reduced-pool test, container CPU was observed at
    approximately **41%**, and Hikari showed **1 waiting thread** at the
    time checked.

## Overall latency comparison

The pre-reduction figures below are the recorded averages of per-run
summary values from the earlier 5-VU baseline runs. Run 6 is one run
with the pool set to 2, so treat this as an indicative comparison rather
than a perfectly controlled statistical comparison.

  Metric                    Earlier 5-VU baseline   Pool size 2, Run 6 (5 VUs)   Change
  ----------------------- ----------------------- ---------------------------- --------
  Average HTTP duration                 49.117 ms                     43.84 ms   -10.7%
  Median HTTP duration                  49.553 ms                     43.53 ms   -12.2%
  p90 HTTP duration                     79.744 ms                     60.64 ms   -24.0%
  p95 HTTP duration                     93.664 ms                     67.38 ms   -28.1%
  p99 HTTP duration                    133.712 ms                    111.76 ms   -16.4%

These results do **not** show an overall latency increase after reducing
the pool. They do not prove that a smaller pool improves performance:
run-to-run variation, warm-up, and test conditions can affect the
comparison.

## Per-endpoint latency comparison

The earlier endpoint values are averages across baseline runs. The
reduced-pool figures are from Run 6.

  -------------------------------------------------------------------------------
  Endpoint        Baseline Pool 2 avg   Baseline Pool 2 p95   Baseline Pool 2 p99
                       avg                   p95                   p99 
  ------------- ---------- ---------- ---------- ---------- ---------- ----------
  Author search  22.202 ms   20.73 ms  28.771 ms   30.59 ms  48.454 ms   38.33 ms

  Member         52.656 ms   46.03 ms  76.985 ms   65.72 ms  90.159 ms   83.62 ms
  records                                                              

  Member         71.848 ms   58.77 ms 115.640 ms   63.79 ms 148.594 ms  110.86 ms
  transaction                                                          
  records                                                              
  -------------------------------------------------------------------------------

The endpoint results are mixed: author-search p95 is slightly higher,
while member-records and member-transaction-records p95 values are
lower. The measurements alone do not establish that pool size caused
these differences.

## Reduced-pool load comparison

  Metric                    Run 6: 5 VUs   Run 7: 10 VUs
  ----------------------- -------------- ---------------
  Requests/sec                      9.21           17.23
  Average HTTP duration         43.84 ms        42.51 ms
  p95 HTTP duration             67.38 ms        72.53 ms
  p99 HTTP duration            111.76 ms       114.63 ms
  Failed HTTP requests                0%              0%
  Completed iterations               883           1,721

Increasing the load from 5 to 10 VUs increased throughput. Overall p95
rose by about 7.6% and p99 by about 2.6%, while no HTTP request failures
were reported. This is a modest latency change in these runs, not
evidence of a severe bottleneck.

## Connection-pool observation

At one point during testing, Hikari reported **1 waiting thread** with a
maximum pool size of 2. This is evidence of connection contention at
that moment: threads needing a database connection had to wait for one
to become available. It does not prove the pool was continuously
exhausted throughout the test.

Container CPU was approximately 41% at the observation point. That is
not enough by itself to conclude that CPU was or was not a bottleneck;
CPU and pool metrics need to be observed during the same load window.

## Conclusion

-   All requests in Runs 6 and 7 succeeded; reported HTTP failure rate
    was **0%**.
-   The reduced-pool runs did not show a large overall latency
    regression.
-   A waiting-thread count of 1 confirms at least some connection
    contention occurred during observation.
-   These tests ran endpoint scenarios sequentially, not all three
    endpoints concurrently. Therefore, they do not fully test
    simultaneous competition for the two connections.
-   Because the baseline values summarize repeated runs and Run 6 is a
    single run, the before/after comparison is indicative, not a strict
    causal result.

## Recommended final validation

For a stronger pool-size comparison, run the **same k6 script, same
endpoint mix, same VUs, same duration, same index state, and same
warm-up** twice: once with the normal pool size and once with pool size
2. Capture p50/p95/p99, throughput, failures, CPU, and Hikari
active/idle/pending connections during each run. Keep the result in the
repository as the C3 portfolio artifact.
