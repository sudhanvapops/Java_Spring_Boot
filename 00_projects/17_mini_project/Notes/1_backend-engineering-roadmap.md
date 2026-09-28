# From CRUD Developer to Backend Systems Engineer
### A staged curriculum ending in CodeCrafts Deploy and a Distributed S3 Clone

---

## Part 0 — How to read this, and what I'm actually optimizing for

You asked me to mentor you rather than list technologies, so let me start with the thing a list would hide.

Your two flagship projects are not the same kind of system, and that matters more than anything else in this document.

**CodeCrafts Deploy is a control-plane problem.** Its hard parts are: running other people's untrusted code safely, managing long-lived processes you didn't write, streaming output from something still running, and recovering when a build dies halfway. Almost none of that is "distributed systems." It's Linux, process isolation, job orchestration, and state machines.

**The Distributed S3 Clone is a data-plane problem.** Its hard parts are: deciding which machine a byte lives on, keeping copies in sync, and staying correct when a node disappears mid-write. This one *is* distributed systems, and it's genuinely harder.

They share a common substrate — asynchronous job execution, containers, queues, observability — which is why the roadmap builds that substrate first and then forks.

Three principles I'm applying throughout:

1. **Every concept is introduced only after you've felt the pain it solves.** You will build a placement function with `hash(key) % N`, add a node, watch every key remap, and *then* learn consistent hashing. Learning it before that is memorization.
2. **Build the manual version first, every single time.** Before CodeCrafts Deploy exists, you will deploy an app by hand and write down all 14 commands. That transcript is your specification.
3. **I'll give you the shape of the problem and the failure modes, not the solution.** Where I sketch a design, I'm sketching what to build — the "how" is the part that teaches you. If you get stuck for more than a day on a specific thing, that's when to ask for a hint.

**Time honesty:** this is roughly 12–15 months at 10–15 focused hours/week. If someone tells you it's a 3-month path, they're describing tutorial-following, not engineering. The phase estimates below assume that pace.

---

## Part 1 — The spine

This is your requested shape, filled in. Everything in Part 3 hangs off this.

```
Library Management System  (you are here — but harden it first)
        ↓  Phase 0: foundations you think you already have
LMS v2
        ↓  Phase 1: asynchrony, jobs, state machines
PROJECT 1 — TaskForge          (a generic background job engine)
        ↓  Phase 2: Linux, processes, Docker, streaming
PROJECT 2 — DockerLab          (sandboxed container runner + live logs)
        ↓  Phase 3
★ FLAGSHIP 1 — CodeCrafts Deploy  (monolith)
        ↓  Phase 4: queues, gateways, caching, observability
★ FLAGSHIP 1 — CodeCrafts Deploy  (microservices)
        ↓  Phase 5: storage engineering
PROJECT 3 — MiniDrive          (chunked, content-addressable storage)
        ↓  Phase 6: distributed systems fundamentals
PROJECT 4 — MiniDynamo         (consistent hashing, replication, quorum)
        ↓  Phase 7
★ FLAGSHIP 2 — Distributed S3 Clone
```

**Why these four and not others.** Each spine project is a *load-bearing subsystem* of a flagship, extracted and built standalone where it's small enough to actually understand:

| Spine project | Is literally the ___ of ___ |
|---|---|
| TaskForge | build queue of CodeCrafts, replication queue of S3 |
| DockerLab | build engine of CodeCrafts |
| MiniDrive | single-node version of the S3 clone |
| MiniDynamo | placement + replication layer of the S3 clone |

That's the trick. You're not doing warm-up projects, you're building the flagships in pieces, from the outside in. When you start CodeCrafts Deploy you'll already own two of its five hardest components.

Part 3 lists five more optional projects (gateway, caching, events, observability, auth) that you can either build standalone or fold into a flagship. I'll say which I recommend.

---

## Part 2 — The phases

Each phase gives you: concepts, why they exist, how deep to go, the project, skills gained, flagship payoff, and **exit criteria** — the tasks you must be able to do without a tutorial before moving on.

---

### PHASE 0 — Re-learn the fundamentals you think you have
**~3–4 weeks · deliverable: LMS v2**

You have CRUD, Spring Security, JPA, validation. Good. But CRUD apps forgive a lot of sins that distributed systems punish immediately. A deployment platform that loses a build record because of a sloppy transaction boundary is worse than no platform, because now it lies to you.

**Concepts, with the depth you need:**

| Concept | Depth | Why it exists |
|---|---|---|
| Transactions, isolation levels, `@Transactional` propagation | **Production** | You will later have two workers grabbing the same job. Understanding `READ COMMITTED` vs `REPEATABLE READ` and row locks is what stops that from double-deploying. |
| Indexes, query plans (`EXPLAIN ANALYZE`), N+1 queries | **Production** | Your S3 metadata service will do millions of chunk lookups. A missing index there isn't slow, it's fatal. |
| Connection pooling (HikariCP), pool sizing | Intermediate | Pool exhaustion is the #1 way Spring services die under load, and it looks like a hang, not an error. |
| Flyway/Liquibase migrations | **Production** | `ddl-auto: update` is fine for learning and unacceptable in anything you'd show an interviewer. |
| Testcontainers + JUnit 5 + Mockito | **Production** | You cannot iterate on distributed logic without tests that spin up real Postgres/Redis/Kafka. This unlocks everything later. |
| Structured error contracts (RFC 7807 / Problem Details) | Intermediate | Services that consume your API need machine-readable errors, not stack traces. |
| Externalized config, profiles, 12-factor | Intermediate | CodeCrafts Deploy *is* a 12-factor enforcement engine. Hard to build one if you don't live by it. |
| Bean lifecycle, `@Transactional` self-invocation trap, proxying | Basic→Intermediate | Explains 80% of "why is my annotation not working". |

**Project: LMS v2.** Not a resume headline — a foundation. Rebuild your existing project with: Flyway migrations, Testcontainers-backed integration tests (aim for the critical paths, not a coverage number), cursor-based pagination, Problem Details errors, a `books` search endpoint that you deliberately make slow, then profile with `EXPLAIN ANALYZE` and fix with an index. Then run a load test (k6 or Gatling) and record p50/p95/p99 before and after.

**Skills gained:** reading a query plan, writing tests you trust, and the habit of measuring instead of guessing.

**Exit criteria — you should be able to, without a tutorial:**
- Explain what happens when two transactions update the same row under `READ COMMITTED`, and demonstrate it with two psql sessions.
- Write a Testcontainers integration test from a blank file in under 20 minutes.
- Take a slow endpoint, find the cause via `EXPLAIN ANALYZE`, fix it, and prove the fix with a load test.
- Explain why `@Transactional` silently does nothing when a method calls another method in the same class.

**Read:** *Use the Index, Luke* (free, online) — the single highest-ROI thing on this list. Spring docs on transaction propagation. Skip books on Spring Boot itself; you're past that.

---

### PHASE 1 — Asynchrony, jobs, and state machines
**~4–5 weeks · deliverable: PROJECT 1, TaskForge**

Here's the realization this phase is built around: **both of your flagships are job systems wearing different costumes.** A build is a job. A deploy is a job. Replicating a chunk to a third node is a job. Re-replicating after a node dies is a job. If you build one excellent job engine, you've built the engine room of both flagships.

**Concepts:**

| Concept | Depth | Why it exists |
|---|---|---|
| Thread pools, `ExecutorService`, Spring `@Async`, `TaskExecutor` | Intermediate | You need to know why an unbounded pool is a memory leak and why the default `SimpleAsyncTaskExecutor` is a trap. |
| The 202-Accepted pattern | **Production** | HTTP requests time out; builds take 4 minutes. Every long operation returns an ID immediately and is polled or streamed. This is *the* API shape of both flagships. |
| Job state machines | **Production** | `QUEUED → RUNNING → SUCCEEDED/FAILED/CANCELLED` with explicit legal transitions. Enforce it in code, not in your head. |
| Idempotency & idempotency keys | **Production** | Retries are guaranteed. If a retry re-runs a deploy, you've shipped twice. Non-negotiable skill. |
| At-least-once vs at-most-once vs exactly-once | Intermediate | Understand that exactly-once delivery doesn't exist, and that at-least-once + idempotent handler is how everyone actually gets it. |
| Retries, exponential backoff, jitter, dead-letter queues | **Production** | Without jitter, all your retries stampede at the same instant and take the system down again. |
| DB-backed queues: `SELECT ... FOR UPDATE SKIP LOCKED` | **Production** | This is how you build a real queue on Postgres. Learn this *before* Kafka. Most companies would be fine with it. |
| Optimistic locking, `@Version` | Intermediate | Two workers, one job row. This is your defense. |
| Graceful shutdown, in-flight job handling | Intermediate | A worker gets SIGTERM mid-build. What happens to the job? If your answer is "it's stuck in RUNNING forever," you have a bug. |
| Leases / heartbeats for stuck jobs | Intermediate | Worker dies without SIGTERM. Job needs to become claimable again after a lease expires. |

**Deliberately NOT in this phase:** Kafka, RabbitMQ, Redis. You will build the queue on Postgres and feel its limits. Adopting a broker before that is cargo-culting.

**PROJECT 1 — TaskForge**
> A standalone background job execution service. `POST /jobs` with a job type and payload → `202` + job ID. Workers claim jobs, execute handlers, report status. Clients poll `GET /jobs/{id}` or subscribe.

Build in this order, and don't skip ahead:
1. Single-process, in-memory queue, one handler type. Works. Loses everything on restart.
2. Persist jobs to Postgres. Restart mid-job — observe the job stuck in `RUNNING` forever. Now you understand leases.
3. Add leasing + heartbeats. Stuck jobs become reclaimable.
4. Multiple worker processes against one DB. Use `SKIP LOCKED`. Prove no double-execution with a test that runs 4 workers and 1000 jobs and asserts exact-once side effects.
5. Retries with exponential backoff + jitter, max attempts, dead-letter table.
6. Idempotency keys so the same submission twice creates one job.
7. Priorities, scheduled/delayed jobs, cancellation (cooperative — the handler must check a flag).
8. Metrics: queue depth, wait time, execution time, failure rate.

**Resume line:** *"Built a distributed job execution engine on Postgres using `SKIP LOCKED` for lock-free work claiming, with lease-based failure recovery, exponential backoff with jitter, and idempotent submission — verified exactly-once side effects under concurrent multi-worker load."*

**Skills gained:** concurrency reasoning, failure-first design, the discipline of modeling state explicitly.

**Prepares you for:** Both flagships. This code is nearly liftable into CodeCrafts as the build queue.

**Exit criteria:**
- Run 4 workers and 1000 jobs; every job executes exactly once. Prove it with a test, not by squinting at logs.
- `kill -9` a worker mid-job; the job recovers and completes on another worker.
- Explain, unprompted, why exactly-once *delivery* is impossible and what you did instead.
- Draw your job state machine on a whiteboard including every illegal transition.

---

### PHASE 2 — Linux, processes, containers, and streaming
**~5–6 weeks · deliverable: PROJECT 2, DockerLab**

CodeCrafts Deploy's real job is running code you did not write and do not trust. That's a Linux problem before it's a Spring problem, and it's the phase most people skip — which is exactly why doing it properly separates you.

**Concepts:**

| Concept | Depth | Why it exists |
|---|---|---|
| Processes, fork/exec, PIDs, signals, exit codes | Intermediate | You're going to spawn hundreds of processes. `SIGTERM` vs `SIGKILL` is the difference between a clean shutdown and corrupt state. |
| stdout/stderr, pipes, file descriptors, buffering | **Production** | Why do build logs arrive in a 4KB burst instead of line by line? Block buffering. You will hit this. |
| Java `ProcessBuilder`, stream draining, deadlocks | **Production** | Fail to drain stdout and the child process blocks forever when the pipe buffer fills. Classic, painful, worth experiencing once. |
| Namespaces & cgroups (conceptually) | Intermediate | This is what a container *actually is*. Not "a lightweight VM." Namespaces = what it can see; cgroups = what it can use. |
| Docker: images, layers, build cache, volumes, networks | **Production** | Your build engine's performance is layer-cache performance. |
| Multi-stage builds, `.dockerignore`, image size | **Production** | Directly determines your platform's deploy speed. |
| Docker API (docker-java or the HTTP socket) | **Production** | You'll drive Docker programmatically, not by shelling out. |
| Resource limits: `--memory`, `--cpus`, `--pids-limit`, timeouts | **Production** | A user submits `while(true) fork()`. Your platform must survive. |
| Container security: non-root, `--network none`, read-only rootfs, dropped capabilities, seccomp | Intermediate | You're running untrusted code. Take this seriously — it's also a fantastic interview topic. |
| SSE vs WebSockets vs long polling | Intermediate | Live build logs are one-directional server→client. SSE is the right, simpler answer. Know why you didn't pick WebSockets. |
| Backpressure & offset-based log resume | Intermediate | Client reconnects mid-build; must resume at the right byte, not restart. |

**PROJECT 2 — DockerLab**
> Submit source code or a Git repo → it runs inside a locked-down container with hard resource limits → output streams back live → you get an exit code and artifacts.

Build order:
1. `ProcessBuilder` runs `echo hello`. Capture stdout. Then deliberately run something producing 10MB of output without draining the stream, and watch it deadlock. Fix it. This lesson costs an hour and saves you a week later.
2. Run it in a Docker container instead, via the Docker API.
3. Add limits: memory, CPU, pids, wall-clock timeout, no network, non-root, read-only filesystem. Then *attack your own system*: fork bomb, memory bomb, infinite loop, a container trying to write to `/`, one trying to phone home. Each attack must fail gracefully. Write these as tests.
4. Stream logs out over SSE while the container runs.
5. Persist logs with byte offsets; support `Last-Event-ID` reconnect.
6. Accept a Git repo (JGit), detect the build tool from files present (`pom.xml`, `package.json`, `go.mod`), run the right build inside the container, produce an artifact.
7. Wire it to TaskForge so submissions are jobs, not blocking calls.

**Resume line:** *"Built a sandboxed code execution service running untrusted workloads in resource-constrained containers (cgroup memory/CPU/PID limits, no network, read-only rootfs, non-root) with live SSE log streaming and offset-based reconnection; hardened against fork bombs, memory exhaustion, and container escape attempts with an automated adversarial test suite."*

That adversarial test suite is the sentence that gets you the interview. Most candidates cannot say anything like it.

**Skills gained:** systems-level thinking, security-mindedness, treating the OS as an API.

**Prepares you for:** CodeCrafts Deploy — this *is* its build engine.

**Exit criteria:**
- Explain what a container is in terms of namespaces and cgroups without saying "lightweight VM."
- Your adversarial suite passes: fork bomb, OOM, infinite loop, network egress attempt, filesystem write attempt — all contained, all reported cleanly.
- Kill the client mid-stream, reconnect, and resume logs at the exact right offset.
- Explain why you chose SSE over WebSockets.

**Read:** *The Linux Programming Interface* (Kerrisk) — chapters on processes and signals only; treat it as reference, not cover-to-cover. Julia Evans' zines on containers and networking are outstanding and short.

---

### PHASE 3 — ★ FLAGSHIP 1: CodeCrafts Deploy (monolith)
**~8–10 weeks**

You now own the job engine and the build engine. This phase assembles them into a platform and adds the parts you haven't built: routing, runtime lifecycle, and Git integration.

**New concepts introduced here (deliberately few — that's the point):**

| Concept | Depth | Why |
|---|---|---|
| Reverse proxies & dynamic routing (Caddy / Traefik / Nginx) | **Production** | Something must map `myapp.codecrafts.dev` → container on port 34871. That "something" is the heart of the platform. |
| Zero-downtime deploys (blue-green at container level) | **Production** | Start new, health-check it, switch routing, drain and kill old. This is the single most impressive part of the project. |
| Health checks & readiness vs liveness | Intermediate | "Started" ≠ "ready for traffic." Cutting over too early is a self-inflicted outage. |
| Webhooks + HMAC signature verification | **Production** | GitHub webhooks are unauthenticated HTTP until you verify the signature. Getting this right is a security-literacy signal. |
| Secrets at rest: envelope encryption | Intermediate | Env vars are secrets. Encrypt with a data key, encrypt the data key with a master key. Never log them, never return them via API. |
| TLS & ACME / Let's Encrypt automation | Basic→Intermediate | Custom domains need certs. Caddy does this for you — understanding *what* it's doing is the learning. |
| Immutable artifacts, tagging by commit SHA | **Production** | Tag every image with its commit SHA and rollback becomes trivial: re-run an old SHA. Design decision that erases a whole feature's complexity. |

#### Milestones

**M0 — Do it by hand.** Deploy a real app manually. Write down every command, every failure, every "wait, I have to also do X." That transcript is your requirements document. Do not skip this.

**M1 — Blocking, ugly, working.** `POST /deploy {repoUrl}` → clone → build → return logs. Synchronous. One repo. It will time out on real projects. Good — that's the lesson.

**M2 — Make it a job.** Plug in TaskForge. Returns `202` + deployment ID. Deployment state machine: `QUEUED → CLONING → BUILDING → PUSHING → DEPLOYING → HEALTHCHECKING → RUNNING | FAILED`. Every transition persisted and timestamped.

**M3 — Containerized builds.** Plug in DockerLab. Detect or generate a Dockerfile. Produce an image tagged `app-{id}:{commitSha}`. Builds are now reproducible and isolated.

**M4 — Runtime lifecycle.** Start the container, allocate a port, register the route with your reverse proxy's API, health-check until ready, *then* switch traffic, then drain and remove the old container. Now you have zero-downtime deploys.

**M5 — Logs & history.** Live build logs via SSE (from DockerLab), persisted with offsets. Deployment history per project. **Rollback** — which, because of M3's SHA tagging, is just "deploy this old image," a few lines of code.

**M6 — GitHub integration.** OAuth or GitHub App. Webhooks with HMAC verification. Auto-deploy on push to a configured branch. Post deployment status back to the commit. Branch → environment mapping (`main` → prod, others → preview).

**M7 — Config & domains.** Environment variables, encrypted at rest, injected at container start, redacted in all logs. Custom domains with automatic TLS.

**M8 — Multi-tenancy & survival.** Per-user concurrent build limits and quotas. Build timeouts. Disk GC for old images and artifacts (you *will* fill the disk — plan for it before it happens). Audit log.

**M9 — Operate it.** Metrics: build duration p50/p95, queue depth, success rate, active containers, disk usage. A Grafana dashboard. Then chaos-test: kill a worker mid-build, kill Docker, fill the disk, push a repo that doesn't compile, push one with a fork bomb. Every failure must be handled and *visible*.

**Exit criteria:** a friend pushes to their GitHub repo and, without you touching anything, the app is live on a URL with TLS in under three minutes — and when they push broken code, the running version stays up and they get a clear failure log.

---

### PHASE 4 — Distributed communication: queues, gateways, caching, observability
**~6–8 weeks · deliverable: CodeCrafts Deploy, microservice edition**

Now — and only now — you split the monolith. The order matters: you split it *because you've felt the constraint*, which means you can explain the decision. Splitting first would leave you with distributed-systems problems and no distributed-systems reason.

**The forcing question:** your build workers need beefy machines with Docker. Your API needs to be highly available and cheap. They have completely different scaling profiles and failure domains. *That* is a real reason to split, and it's the answer you give in interviews.

**Concepts:**

| Concept | Depth | Why |
|---|---|---|
| Message brokers: RabbitMQ vs Kafka | Intermediate→Production | Learn the *difference in model*: RabbitMQ = smart broker, dumb consumer, work queues. Kafka = dumb broker, smart consumer, durable ordered log, replayable. For a build queue, RabbitMQ fits. For an event log/audit stream, Kafka fits. |
| Consumer groups, partitions, ordering guarantees | **Production** | Ordering is per-partition only. Choosing a partition key is a design decision with correctness consequences. |
| The transactional outbox pattern | **Production** | You cannot atomically write to Postgres *and* publish to Kafka. The outbox is how everyone solves it. Extremely common interview question. |
| Idempotent consumers | **Production** | Redelivery is normal, not exceptional. |
| API Gateway: routing, authn, rate limiting | **Production** | One public door. Auth once at the edge. |
| Rate limiting: token bucket, sliding window, distributed counters in Redis | **Production** | Protects you from your own users. |
| Redis: cache-aside, TTL, invalidation, stampede protection | **Production** | And the failure modes: hot keys, thundering herd on expiry, cache/DB inconsistency windows. |
| Resilience: timeouts, retries, circuit breakers, bulkheads (Resilience4j) | **Production** | A service without a timeout will eventually hang forever and take its callers with it. Timeouts are the highest-value item here. |
| Service discovery, service-to-service auth | Intermediate | How does Build Service find Log Service when there are six replicas? |
| Observability: metrics (Micrometer/Prometheus), structured logs, correlation IDs, tracing (OpenTelemetry/Jaeger) | **Production** | Once a request spans six services, logs alone can't answer "where did the 4 seconds go?" Tracing can. |
| Idempotency across service boundaries, saga/compensation basics | Intermediate | Distributed transactions don't exist. Compensating actions do. |

**Target architecture:**

```
                       API Gateway  (authn, rate limit, routing)
                             │
   ┌──────────┬──────────┬───┴──────┬───────────┬────────────┐
 Auth      Project    Build       Runtime      Log        Notification
Service    Service   Orchestrator  Service    Service      Service
                          │            │
                       [ Broker ]      └── reverse proxy config API
                          │
                  ┌───────┴───────┐
              Worker 1        Worker N     ← separate machines, Docker hosts
```

**Migration order (strangler pattern — never a big-bang rewrite):**
1. Extract **Worker** first. It's already isolated behind a queue; this is the low-risk cut that proves the pattern.
2. Introduce the **broker** properly (replace the Postgres queue for build dispatch — and be able to explain what you gained and what you lost).
3. Extract **Log Service** — high volume, different storage profile, natural boundary.
4. Add the **Gateway**, move auth to the edge.
5. Extract **Auth**, then **Runtime**.
6. Add tracing across all of it. Then run a request and look at the flame graph. This moment is when distributed systems stop being abstract.

**Exit criteria:**
- Kill any single service; explain and demonstrate exactly what degrades and what survives.
- Trace one deployment end-to-end across all services in Jaeger with a single correlation ID.
- Explain out loud why you chose your broker, what the outbox pattern solved, and what you'd do differently.
- Explain what you *lost* by splitting the monolith. If you can't name three things, you haven't learned the lesson.

**Read:** *Designing Data-Intensive Applications* (Kleppmann), chapters 1–4 and 11. Start it here, not earlier — before this phase it reads as trivia; now it reads as answers to questions you already have.

---

### PHASE 5 — Storage engineering
**~4–5 weeks · deliverable: PROJECT 3, MiniDrive**

Fork point. CodeCrafts is done; now you build toward S3. This phase is deliberately single-node — get storage right on one machine before distributing it.

**Concepts:**

| Concept | Depth | Why |
|---|---|---|
| Streaming I/O, never buffering a whole file in memory | **Production** | A 5GB upload must not be a 5GB heap allocation. `InputStream` end-to-end. |
| Content-addressable storage (hash = identity) | **Production** | Store chunks by their SHA-256. Deduplication becomes free. Integrity becomes free. Immutability becomes free. One of the most elegant ideas in storage. |
| Chunking & manifests | **Production** | Big objects become lists of chunk hashes. Enables resume, parallelism, dedupe, and later, distribution. |
| Checksums & integrity verification | **Production** | Verify on write and on read. Disks lie silently; this is how you catch it. |
| Multipart & resumable uploads | **Production** | Network dies at 90%. Restarting from zero is unacceptable. |
| HTTP range requests (206 Partial Content) | Intermediate | Video seeking, resumable downloads. |
| Presigned URLs (HMAC-signed, expiring) | **Production** | Lets clients upload/download directly without your service proxying bytes or handing out credentials. |
| ETags, conditional requests, optimistic concurrency | Intermediate | S3-compatible semantics start here. |
| Metadata vs data separation | **Production** | Metadata is small, queryable, transactional (Postgres). Data is huge, immutable, dumb (disk). Conflating them is the classic beginner mistake. |
| Garbage collection: mark & sweep, orphaned chunks | Intermediate | Delete an object; its chunks may be shared. When is it safe to actually remove bytes? Genuinely subtle — races with concurrent uploads. |

**PROJECT 3 — MiniDrive.** Single-node object store: buckets, streaming upload/download, chunking, content-addressed chunk store, SHA-256 verification, multipart + resumable uploads, range requests, presigned URLs, metadata in Postgres, and a GC job (on TaskForge) that reclaims orphaned chunks.

**Test that matters:** upload the same 1GB file twice, verify the second consumes ~0 additional disk. Then corrupt a chunk on disk by hand and verify the read fails loudly rather than returning bad data.

**Resume line:** *"Built a content-addressable object store with SHA-256 chunk deduplication, resumable multipart uploads, range-request support, HMAC presigned URLs, and a concurrency-safe mark-and-sweep garbage collector."*

**Exit criteria:**
- Upload 5GB with a constrained JVM heap (`-Xmx256m`). If it OOMs, you're buffering somewhere.
- Kill an upload at 60% and resume it successfully.
- Corrupt a chunk on disk; the read detects it and fails loudly.
- Explain how your GC avoids deleting a chunk that a concurrent upload is currently referencing.

---

### PHASE 6 — Distributed systems fundamentals
**~6–8 weeks · deliverable: PROJECT 4, MiniDynamo**

The hardest phase. Everything before this had one source of truth. Now the truth is spread across machines that can't fully trust each other and can't tell "dead" from "slow."

**Concepts:**

| Concept | Depth | Why |
|---|---|---|
| The eight fallacies of distributed computing | **Production** | Memorize them. They are the failure list you'll design against forever. |
| Partial failure & the impossibility of distinguishing slow from dead | **Production** | The single most important idea in this phase. Every timeout you pick is a guess about this. |
| Consistent hashing + virtual nodes | **Production** | Solves: "I added a node and every key remapped." **Build `hash % N` first, add a node, watch the catastrophe, then learn this.** |
| Replication factor, N/W/R quorums | **Production** | `W + R > N` gives read-your-writes. Tune W and R to trade latency against consistency. |
| CAP, and the more useful PACELC | Intermediate | CAP is over-quoted and under-understood. PACELC (else, latency vs consistency) describes real systems better. |
| Failure detection: heartbeats, timeouts, phi-accrual | Intermediate | You'll implement simple timeout-based detection and understand its false-positive problem. |
| Hinted handoff | Intermediate | Target node down at write time — write a hint elsewhere, deliver later. Dynamo's trick. |
| Anti-entropy & Merkle trees | Intermediate | How replicas find their differences without comparing every key. |
| Conflict resolution: LWW, vector clocks, version vectors | Intermediate | Two writes, no coordination. Who wins? LWW is simple and lossy; vector clocks are correct and complex. Know the tradeoff. |
| Read repair | Intermediate | Fix staleness opportunistically during reads. Cheap and effective. |
| Consensus (Raft) — conceptual only | **Basic** | Understand leader election and log replication *conceptually*. **Do not implement Raft.** It's a months-long detour. Know when you'd reach for etcd instead. |
| Rebalancing on membership change | Intermediate | Adding a node must move only ~1/N of the data. |

**PROJECT 4 — MiniDynamo.** A distributed key-value store. Not a toy — the real thing at small scale.

Build order (each step must hurt before the next makes sense):
1. Single node KV store over HTTP. Boring. Works.
2. Three nodes, placement by `hash(key) % 3`. Works.
3. **Add a fourth node.** Watch ~75% of keys become unreachable. Sit with that.
4. Replace with consistent hashing + virtual nodes. Add a fifth node; measure how many keys actually moved. It should be ~1/5.
5. Replication factor 3. Coordinator writes to 3 replicas.
6. Quorum: configurable N/W/R. Run experiments — set `W=1, R=1` and demonstrate a stale read. Then `W=2, R=2` and demonstrate it's gone. *Measuring your own consistency violations* is the most valuable exercise in this entire roadmap.
7. Heartbeats and node state (`UP`/`SUSPECT`/`DOWN`). Then introduce artificial network latency and watch your detector produce false positives.
8. Hinted handoff for writes to down nodes.
9. Read repair on divergent reads.
10. Anti-entropy: periodic replica comparison (start with simple checksum-per-range, then Merkle trees if you want the challenge).
11. Rebalancing when a node joins or leaves permanently.

**Resume line:** *"Implemented a Dynamo-style distributed key-value store with consistent hashing over virtual nodes, tunable N/W/R quorums, heartbeat-based failure detection, hinted handoff, read repair, and Merkle-tree anti-entropy — with a fault-injection harness demonstrating consistency behavior under partition."*

**Exit criteria:**
- Kill a node mid-write with `W=2, N=3`; the write succeeds and the data reconciles when the node returns.
- Demonstrate a stale read at `W=1, R=1` and its absence at `W=2, R=2`, with a reproducible test.
- Add a node and show that approximately `1/N` of keys moved, with numbers.
- Explain why you cannot tell a dead node from a slow one, and what your system does about that ambiguity.

**Read:** the Amazon Dynamo paper (2007) — genuinely readable, and after this project you'll understand it deeply rather than nodding along. Then *DDIA* chapters 5, 6, 7, 9.

---

### PHASE 7 — ★ FLAGSHIP 2: Distributed S3 Clone
**~10–12 weeks**

MiniDrive gave you storage. MiniDynamo gave you distribution. This is the merge — plus the things unique to object storage at scale: durability, scrubbing, and API compatibility.

#### Milestones

**M0 — Study the API you're cloning.** Read the S3 REST API docs. Decide your subset: buckets, PUT/GET/DELETE/HEAD object, list with prefix and pagination, multipart upload. Write it down as a spec before coding.

**M1 — Lift MiniDrive** as the single-node baseline. It already does chunking, content addressing, and checksums.

**M2 — Split into Metadata Service + Storage Node.** Two deployables. Metadata owns buckets, objects, chunk manifests, and chunk→node mappings (Postgres). Storage Nodes own dumb chunk storage: `PUT /chunks/{hash}`, `GET /chunks/{hash}`, `DELETE`. Keeping storage nodes dumb is the correct architectural decision — make it deliberately.

**M3 — Node registration & health.** Nodes register with capacity, report heartbeats and free space. Metadata tracks node state.

**M4 — Distributed placement.** Consistent hashing from MiniDynamo decides which nodes hold a chunk. Chunk locations recorded in metadata.

**M5 — Replication.** RF=3. Write path: client → metadata (allocate) → parallel writes to 3 nodes → quorum ack → metadata commits the manifest. Get the ordering right: **the object must not be visible until durably replicated.** Read path: pick the healthiest replica, fall back on failure.

**M6 — Failure & recovery.** Node goes down → detect → find all under-replicated chunks → enqueue re-replication jobs on TaskForge → restore RF. Node comes back → reconcile, don't duplicate. Node added → rebalance.

**M7 — Durability engineering.** Background scrubber walks chunks, re-verifies SHA-256, detects bit rot, repairs from a good replica. Metrics for under-replicated chunk count and scrub lag. Distributed GC — deleting an object's chunks safely across nodes, respecting dedupe references and racing uploads. This is genuinely the hardest correctness problem in the project; take it slowly.

**M8 — Scale & operate.** API Gateway. Redis cache for hot metadata. Metadata read replicas. Metrics: upload/download throughput, p99 latency, replication lag, under-replication, per-node capacity. Grafana dashboards.

**M9 — Chaos.** Kill a node mid-upload. Partition the network. Fill a disk. Corrupt chunks on disk deliberately. Kill metadata during a commit. For each: the system must either succeed or fail cleanly with no data loss and no silent corruption. Document the results — *this document is the most impressive artifact in your portfolio.*

**M10 — Stretch goals** (pick one, not all):
- **AWS Signature V4 compatibility** so the real AWS SDK and `aws s3 cp` work against your system unmodified. Enormously high signal — you can demo it live in an interview.
- **Erasure coding** (Reed-Solomon) instead of 3x replication: same durability at ~1.5x storage instead of 3x. This is what real S3 does.
- Bucket lifecycle policies and storage tiers.

**Exit criteria:** upload a 5GB file, kill a storage node mid-transfer, and get a byte-perfect download afterward — with your monitoring showing exactly what happened and the system self-healing back to full replication without you touching it.

---

## Part 3 — The project catalog

Nine intermediate projects. **Four are spine** (do these). **Five are bolt-ons** — build standalone if you want the depth and the resume line, or fold into a flagship if you're moving fast. My recommendation on each is noted.

---

### SPINE ①  TaskForge — Distributed Job Execution Engine
- **Purpose:** Own the async execution model that both flagships run on.
- **Tech:** Spring Boot, Postgres (`SKIP LOCKED`), JPA, Testcontainers, Micrometer.
- **New concepts:** thread pools, job state machines, leasing & heartbeats, idempotency keys, exponential backoff with jitter, dead-letter queues, optimistic locking, graceful shutdown.
- **Difficulty:** 4/10
- **Time:** 3–4 weeks
- **Prepares:** **Both.** Build queue in CodeCrafts; replication and GC queues in S3.

### SPINE ②  DockerLab — Sandboxed Container Execution Service
- **Purpose:** Run untrusted code safely and stream its output live.
- **Tech:** Spring Boot, docker-java, JGit, SSE, Linux cgroups/namespaces.
- **New concepts:** `ProcessBuilder` and stream draining, signals, cgroup resource limits, container hardening, SSE streaming, offset-based log resume, build-tool detection.
- **Difficulty:** 6/10
- **Time:** 4–5 weeks
- **Prepares:** **CodeCrafts Deploy** (it *is* the build engine).

### SPINE ③  MiniDrive — Content-Addressable Object Store
- **Purpose:** Get single-node storage correct before distributing it.
- **Tech:** Spring Boot, Postgres, streaming I/O, SHA-256, HMAC.
- **New concepts:** content addressing, chunking & manifests, dedupe, checksums, multipart/resumable upload, range requests, presigned URLs, metadata/data separation, mark-and-sweep GC.
- **Difficulty:** 5/10
- **Time:** 3–4 weeks
- **Prepares:** **Distributed S3 Clone** (it *is* v1).

### SPINE ④  MiniDynamo — Distributed Key-Value Store
- **Purpose:** Learn distribution on a small, comprehensible data model before applying it to large objects.
- **Tech:** Spring Boot, multi-node deployment, HTTP or gRPC between nodes, fault-injection harness.
- **New concepts:** consistent hashing with virtual nodes, replication factor, N/W/R quorums, failure detection, hinted handoff, read repair, anti-entropy, Merkle trees, rebalancing.
- **Difficulty:** 8/10
- **Time:** 5–6 weeks
- **Prepares:** **Distributed S3 Clone** (its placement and replication layer).

---

### BOLT-ON ⓐ  GatewayLite — API Gateway & Rate Limiter
- **Purpose:** Own the edge: routing, auth, throttling, resilience.
- **Tech:** Spring Cloud Gateway, Redis, Resilience4j.
- **New concepts:** reverse proxying, token bucket & sliding window rate limiting, distributed counters, API keys, circuit breakers, bulkheads, timeout budgets.
- **Difficulty:** 6/10 · **Time:** 2–3 weeks
- **Prepares:** Both. **Recommendation: build standalone during Phase 4.** Rate limiting deserves its own focus and is a frequent interview topic.

### BOLT-ON ⓑ  PulseCache — Cache Layer for a Read-Heavy Service
- **Purpose:** Learn caching by breaking it, not by adding `@Cacheable`.
- **Tech:** Redis, Spring Data Redis, k6.
- **New concepts:** cache-aside vs write-through, TTL strategy, invalidation, stampede protection (locking, probabilistic early expiry), hot keys, negative caching, hit-rate measurement.
- **Difficulty:** 5/10 · **Time:** 2 weeks
- **Prepares:** S3 metadata caching. **Recommendation: fold into Phase 4/8**, unless you want to demonstrate stampede protection specifically — it's a strong differentiator.

### BOLT-ON ⓒ  EventHub — Event-Driven Notification Service
- **Purpose:** Learn brokers properly, including the outbox pattern.
- **Tech:** Kafka *or* RabbitMQ, Spring, Postgres, Testcontainers.
- **New concepts:** consumer groups, partitions, ordering guarantees, offset management, idempotent consumers, DLQs, transactional outbox, schema evolution.
- **Difficulty:** 6/10 · **Time:** 3 weeks
- **Prepares:** Both. **Recommendation: build standalone.** The outbox pattern is asked about constantly and is hard to learn well while also doing five other things.

### BOLT-ON ⓓ  Observatory — Observability-First Service
- **Purpose:** Make a system explain itself.
- **Tech:** Micrometer, Prometheus, Grafana, OpenTelemetry, Jaeger, Loki.
- **New concepts:** RED/USE metrics, histograms vs summaries (and why averages lie), structured JSON logging, correlation IDs, distributed tracing, alerting, SLOs and error budgets.
- **Difficulty:** 5/10 · **Time:** 2 weeks
- **Prepares:** Both. **Recommendation: fold into Phase 4**, but do it properly — an operable system is what separates a project from a demo.

### BOLT-ON ⓔ  IdentityHub — Production Auth Service
- **Purpose:** Upgrade from "Spring Security tutorial" to real auth.
- **Tech:** Spring Security, OAuth2, JWT, Redis.
- **New concepts:** access vs refresh tokens, rotation and reuse detection, revocation with stateless tokens, RBAC vs ABAC, OAuth2 flows, PKCE, service-to-service auth (mTLS or client credentials), multi-tenancy.
- **Difficulty:** 5/10 · **Time:** 2–3 weeks
- **Prepares:** Both. **Recommendation: build standalone before Phase 4**, because CodeCrafts needs GitHub OAuth anyway and doing it well once pays twice.

---

**Fast path** (spine only): ①②→ CodeCrafts →③④→ S3 Clone.
**Recommended path:** ①②→ CodeCrafts monolith →ⓔⓐⓒ + CodeCrafts microservices (ⓑⓓ folded in) →③④→ S3 Clone.

---

## Part 4 — Theory: what each thing is actually for

For each: the problem it solves, why companies use it, where it lands in your work, when to learn it, and — importantly — **what to safely ignore for now.** That last column is the one that saves you months.

---

### Docker & containers
**Problem:** "works on my machine." Dependency and environment drift between dev, CI, and prod.
**Why companies use it:** an image is a reproducible, immutable artifact. Deploy the same bytes everywhere. Bin-pack many workloads onto one machine without VM overhead.
**Where it lands:** DockerLab, CodeCrafts build engine and runtime. Non-negotiable.
**When:** Phase 2.
**Ignore for now:** Docker Swarm, Compose for production, custom runtimes, rootless Docker, building your own OCI runtime.

### Kubernetes
**Problem:** running thousands of containers across hundreds of machines with self-healing and declarative desired state.
**Why companies use it:** it's the industry standard control plane; it removes bespoke orchestration glue.
**Where it lands:** **nowhere, until both flagships are done.** Here's the mentor take: CodeCrafts Deploy *is* a container orchestrator. Building one teaches you more about Kubernetes than six months of using it. Deploy on plain Docker hosts.
**When:** after Phase 7, or when a job requires it. Then you'll learn it in two weeks because you'll recognize every concept.
**Ignore for now:** all of it. Operators, CRDs, Helm, service meshes, Istio. Especially service meshes.

### Message queues — RabbitMQ vs Kafka
**Problem:** two services must communicate without being simultaneously available, and slow consumers shouldn't block fast producers.
**The distinction that matters:**
- **RabbitMQ** — smart broker, dumb consumer. Messages are routed, consumed, acknowledged, and *gone*. Excellent for task/work queues where each message is a unit of work. Per-message ack, complex routing, priorities.
- **Kafka** — dumb broker, smart consumer. An append-only, partitioned, durable log. Consumers track their own offset and can replay from any point. Excellent for event streams, audit logs, and multiple independent consumers of the same data.
**Rule of thumb:** "do this task once" → RabbitMQ. "this happened, and several systems care, and I might want to replay it" → Kafka.
**Where it lands:** build dispatch in CodeCrafts (RabbitMQ-shaped); deployment/audit event stream (Kafka-shaped).
**When:** Phase 4 — *after* you've hit the ceiling of a Postgres queue. Note that plenty of production systems never outgrow the Postgres queue. Knowing that is a sign of judgment.
**Ignore for now:** Kafka Streams, ksqlDB, Connect, exactly-once semantics, tiered storage, tuning ISR.

### Redis & caching
**Problem:** the database is the bottleneck; the same expensive read happens ten thousand times.
**Why companies use it:** microsecond in-memory reads; also doubles as distributed lock, rate limiter, session store, and leaderboard.
**Where it lands:** rate limiting in the gateway; hot metadata caching in the S3 clone.
**When:** Phase 4.
**The part that actually matters:** not "how do I cache" but the failure modes — stampede on expiry, hot keys, and the inconsistency window between cache and DB. Caching is easy; *invalidation* is the discipline.
**Ignore for now:** Redis Cluster, Sentinel, Lua scripting, RedisJSON, Streams, persistence tuning.

### Reverse proxy & load balancer
**Problem (reverse proxy):** one public entry point that hides many internal services; handles TLS, routing, and static content.
**Problem (load balancer):** spread traffic across identical replicas and stop sending it to unhealthy ones.
They're often the same box doing both jobs, which is why they get conflated. A reverse proxy routes *by request attributes* (host, path); a load balancer distributes *across identical backends*.
**Where it lands:** the heart of CodeCrafts — dynamically mapping `app.yourdomain.dev` to an ephemeral container port. Use **Caddy** or **Traefik**, both of which expose an API for dynamic config; Nginx needs config regeneration and reload, which you should try once to feel the difference.
**When:** Phase 3.
**Ignore for now:** L4 vs L7 minutiae, HAProxy tuning, anycast, BGP, DSR.

### CI/CD
**Problem:** manual build-and-deploy is slow, inconsistent, and unauditable.
**Where it lands:** you're *building* a CD system. Meanwhile, use GitHub Actions on your own projects — test, build, publish images. Deliberately self-host CodeCrafts using CodeCrafts once it works. That story is a great interview moment.
**When:** Phase 0 lightly, seriously by Phase 3.
**Ignore for now:** Jenkins, ArgoCD/GitOps, Spinnaker.

### gRPC
**Problem:** JSON-over-HTTP is verbose, untyped across service boundaries, and slow for chatty internal calls.
**Why companies use it:** Protobuf gives a compile-time contract and compact binary encoding over HTTP/2, with streaming built in.
**Where it lands:** *optional* — internal calls between metadata service and storage nodes in the S3 clone, where the payload volume is high and the contract is stable.
**When:** Phase 6 at the earliest, and only if you want it. REST is completely acceptable everywhere in these projects.
**Honest take:** gRPC is a nice-to-have here, not a need-to-have. If you're short on time, skip it. Knowing *when* you'd reach for it interviews nearly as well as having used it.
**Ignore for now:** gRPC-Web, bidirectional streaming, deadline propagation subtleties, protobuf schema registries.

### Service discovery
**Problem:** Service A needs Service B's address, but B has six replicas that come and go.
**Options in ascending complexity:** DNS → environment config → client-side discovery (Consul/Eureka) → platform-provided (Kubernetes Services).
**Where it lands:** CodeCrafts microservice phase, and storage node registration in S3 — where you'll implement a simple version yourself (nodes register and heartbeat to metadata), which teaches more than adopting Consul would.
**When:** Phase 4.
**Ignore for now:** Consul's full feature set, service meshes, sidecar proxies.

### Consistent hashing
**Problem:** with `hash(key) % N`, changing N remaps almost every key. Going from 3 nodes to 4 relocates ~75% of your data. Catastrophic.
**The idea:** map both keys and nodes onto a ring; a key belongs to the next node clockwise. Adding a node steals a contiguous arc from exactly one neighbor — only ~1/N of keys move. **Virtual nodes** (each physical node placed at ~150 ring positions) fix the resulting load imbalance.
**Where it lands:** placement in MiniDynamo, then chunk placement in the S3 clone.
**When:** Phase 6 — **and only after you've built modulo hashing and watched it break.** This is the clearest example in the whole roadmap of a concept that's forgettable trivia before the pain and permanent knowledge after it.
**Ignore for now:** rendezvous hashing, jump consistent hash, bounded-load variants.

### Replication, quorums, and CAP
**Problem:** one copy of data means one disk failure equals permanent loss. Multiple copies mean they can disagree.
**The core formula:** with N replicas, W write acks, R read responses — if `W + R > N`, any read set overlaps any write set, so you always see the latest write. `N=3, W=2, R=2` is the classic balanced choice.
**CAP:** during a network partition you choose availability or consistency. You cannot choose "no partition."
**PACELC is more useful:** *if Partitioned, choose A or C; Else, choose Latency or Consistency.* It describes what systems do the other 99.9% of the time.
**Where it lands:** MiniDynamo, then S3 chunk replication.
**When:** Phase 6.
**Ignore for now:** Paxos, Raft implementation, CRDTs, linearizability proofs, serializable snapshot isolation internals.

### Consensus (Raft / etcd / ZooKeeper)
**Problem:** several nodes must agree on one value — who's the leader, what's the current cluster membership — despite failures.
**Where it lands:** honestly, you can complete both flagships without it. Where you'd *want* it: cluster membership in the S3 clone.
**When:** understand it conceptually in Phase 6. **Do not implement Raft.** It's a multi-month project that will stall your roadmap. Read the paper, watch the Raft visualization, be able to explain leader election and log replication. If you genuinely need it later, run etcd.
**Ignore for now:** implementing it. Multi-Paxos. ZooKeeper's API.

### Observability
**Problem:** the system is slow. Which of your six services caused it? Logs alone can't tell you.
**The three pillars:** metrics (aggregate, cheap, "is something wrong?"), logs (detailed, expensive, "what exactly happened?"), traces (causal, "where did the time go across services?").
**Where it lands:** everywhere from Phase 4 onward.
**When:** Phase 4 — but start emitting metrics in Phase 1. It's easier to build in than bolt on.
**The one thing to internalize:** average latency is a lie. Always look at p95 and p99. Your average user experience is fine; your worst 1% is where users churn.
**Ignore for now:** custom exporters, Thanos/Cortex, sampling strategies, eBPF-based tooling.

### Circuit breakers & resilience patterns
**Problem:** a slow dependency doesn't just fail — it holds every calling thread hostage until the whole system is dead. Cascading failure.
**The patterns, in order of value:** **timeouts** (highest value by far — a call with no timeout will eventually hang forever), then retries with backoff and jitter, then circuit breakers (stop calling a failing service, give it room to recover), then bulkheads (isolate thread pools so one bad dependency can't consume them all).
**Where it lands:** Phase 4 onward, everywhere.
**When:** Phase 4.
**Ignore for now:** adaptive concurrency limits, hedged requests, load shedding algorithms.

---

## Part 5 — Traps I want you to avoid

These are the specific ways this plan goes wrong. I've ordered them by how likely they are to catch you.

**1. Starting with the architecture diagram.** The microservice diagrams in your original brief are *destinations*, not starting points. If you begin with eight services, you'll spend three months on plumbing and learn distributed debugging before you've learned distribution. Monolith first, always. Split when a constraint forces it, and be able to name the constraint.

**2. Adopting infrastructure before feeling the pain.** Kafka before the Postgres queue hurts. Consistent hashing before modulo breaks. Redis before the DB is slow. Every one of these is a wasted lesson — you'll have the tool and not the understanding, and an interviewer will find the gap in one question.

**3. Skipping the manual version.** M0 exists in both flagships for a reason. Deploy by hand. Store a file by hand. The transcript of your own fumbling is a better specification than any design doc you'd write cold.

**4. Feature-chasing instead of failure-chasing.** Adding a "teams" feature to CodeCrafts teaches you nothing. Making it survive a worker dying mid-build teaches you everything. **When you're unsure what to build next, build the failure case.** This is the single biggest differentiator between a portfolio project and an engineering project.

**5. Not writing the chaos test.** The most impressive artifact you'll produce is a document titled "what happens when things break," listing each failure you injected, what the system did, and what you fixed. Almost nobody has this. It's disproportionately convincing.

**6. Perfecting instead of shipping.** Each project has exit criteria — hit them and move on. You'll return to these ideas repeatedly; you don't need to exhaust them now. An unfinished perfect S3 clone is worth less than a rough finished one.

**7. Underestimating the security surface of CodeCrafts.** You are running arbitrary code from the internet on your machine. Take container hardening seriously in Phase 2. It's also the most interesting thing you'll be able to talk about — very few candidates have thought about container escape.

**8. Learning Kubernetes to feel productive.** It will feel like progress. It is procrastination dressed as ambition, and it will eat two months. After the flagships, it'll take you two weeks.

---

## Part 6 — Making this count

A note on the actual goal, since these are resume projects.

**What impresses a senior engineer reviewing your GitHub:**
- A README that opens with *the problem and the design decisions*, not installation steps.
- An explicit "tradeoffs and what I'd do differently" section. Nothing signals seniority faster than being able to critique your own work.
- Tests that prove the hard properties — the exactly-once test, the quorum consistency test, the fork-bomb containment test.
- A failure/chaos document.
- Metrics with real numbers: "sustained 400 uploads/sec at p99 180ms on 3 nodes." Measure things. Almost nobody does.

**What doesn't impress anyone:** a long list of technologies in the README, a screenshot-heavy demo, feature count.

**Write as you go.** After each project, write a short post explaining one thing that surprised you — the `ProcessBuilder` deadlock, the moment 75% of your keys vanished when you added a node. Six of those posts is a portfolio in itself, and writing forces you to notice whether you actually understood it.

**Core reading, in the order it becomes useful:**
1. *Use the Index, Luke* — Phase 0, free online
2. Julia Evans' zines on containers, networking, debugging — Phase 2
3. *Designing Data-Intensive Applications*, Kleppmann — start Phase 4, chapters 1–4, then 5–7 and 9 in Phase 6
4. The Dynamo paper (Amazon, 2007) — Phase 6
5. The Google SRE book, chapters on monitoring and SLOs — Phase 4
6. The Raft paper and the raft.github.io visualization — Phase 6, conceptual only
7. The Kafka and S3 API docs — read as reference when you reach them, not before

---

## Part 7 — Timeline at a glance

| Phase | Focus | Deliverable | Weeks |
|---|---|---|---|
| 0 | Fundamentals, testing, DB rigor | LMS v2 | 3–4 |
| 1 | Async, jobs, state machines | **TaskForge** | 4–5 |
| 2 | Linux, Docker, isolation, streaming | **DockerLab** | 5–6 |
| 3 | Assemble the platform | **★ CodeCrafts Deploy (monolith)** | 8–10 |
| 4 | Queues, gateway, cache, observability | **★ CodeCrafts (microservices)** + bolt-ons | 6–10 |
| 5 | Storage engineering | **MiniDrive** | 3–4 |
| 6 | Distributed systems | **MiniDynamo** | 5–6 |
| 7 | Distributed storage at scale | **★ Distributed S3 Clone** | 10–12 |

**Total: ~45–57 weeks**, or 11–14 months at 10–15 hrs/week. Add slack — everyone does, and the estimates above assume things mostly work.

If that number feels long: after Phase 3 you already have one flagship, four strong resume projects, and skills most mid-level backend engineers don't have. **Phase 3 is a legitimate stopping point for job applications.** Phases 5–7 are what make you unusual.

---

## Where to start tomorrow

Not Phase 0 in the abstract. Do this:

1. Open your Library Management System. Add Flyway and one Testcontainers integration test. Today.
2. Write down the ten commands you'd run to deploy a Spring Boot app to a fresh Ubuntu box by hand. Actually run them on a $5 VPS. That transcript is CodeCrafts Deploy's spec, and you'll want it in three months.

Then start Phase 0 properly.

One last thing: when you get stuck — and Phase 6 will stick you — the useful question isn't "how do I do X." It's "what failure is X protecting me from?" Almost every concept in this document is an answer to a failure. Find the failure and the design follows.
