### Exit Question

Question 1 — Load-test diagnosis
You've run a k6 test against your LMS. The results show:
- Throughput stops increasing as you add more virtual users.
- p95 latency rises sharply.
- Error rates begin increasing.
Explain how you would investigate these results to identify the bottleneck. Use the metrics and concepts from your C3 work. Don't guess a specific bottleneck without evidence.

Answer:
Load-test metrics: throughput, p95 latency, error rate, and virtual users.
Infrastructure: CPU, memory, and network saturation.
Application and database: logs, slow queries, locks, and connection-pool exhaustion.

