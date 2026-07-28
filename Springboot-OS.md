# Spring Security & OS Concepts (Mental Model)

> Understanding how Spring Boot, the Operating System, Threads, and `SecurityContextHolder` work together.

---

# 1. Starting a Spring Boot Application

When you run

```bash
java -jar app.jar
```

the Operating System creates **one process**.

```
                Operating System
                      │
                      ▼
        +-------------------------------+
        | Spring Boot Process           |
        | PID: 12345                    |
        +-------------------------------+
```

The process has:

- Text Segment (compiled code)
- Data Segment (static variables)
- Heap (shared objects)
- Stack (for the main thread)

The OS also creates a **PCB (Process Control Block)** containing metadata about the process.

> **Important:** There is only **one PCB** for the Spring Boot application.

---

# 2. Tomcat Creates a Thread Pool

Spring Boot's embedded Tomcat does **not** create a new process for every request.

Instead, it creates a pool of worker threads.

```
Spring Boot Process

├── Thread-1
├── Thread-2
├── Thread-3
├── Thread-4
├── ...
└── Thread-200
```

Each thread has:

- Its own Stack
- Its own Program Counter
- Its own CPU Registers

All threads **share**:

- Heap
- Text Segment
- Static Variables

```
               Spring Boot Process

        +-------------------------------+
        |           Heap                |
        |      (Shared Objects)         |
        +-------------------------------+

        +-------------------------------+
        |          Text Code            |
        +-------------------------------+

          ↑         ↑          ↑

     Thread-1   Thread-2   Thread-3

      Stack      Stack      Stack
```

---

# 3. What Happens When a Request Arrives?

Suppose Alice sends

```
GET /profile
```

Tomcat picks an idle thread.

```
Alice Request
      │
      ▼
Thread-17
```

Thread-17 executes:

```
JWT Filter
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
    ↓
Response
```

When the request finishes,

```
Thread-17
    ↓
Returns to Thread Pool
```

The thread **does not die**.

It simply becomes idle.

---

# 4. Is a New PCB Created?

No.

Only one PCB exists.

Threads have their own scheduling structures (Thread Control Blocks), but they belong to the same process.

```
Spring Boot

1 Process
    │
    ├── 1 PCB
    │
    ├── Thread-1
    ├── Thread-2
    ├── Thread-3
    └── ...
```

---

# 5. Is a Thread Reserved for One User?

No.

Example:

```
Request 1 (Alice)

Thread-5
    ↓
Process Request
    ↓
Return to Pool
```

Later,

```
Request 2 (Bob)

Thread-5
    ↓
Process Request
    ↓
Return to Pool
```

The same thread can serve hundreds of different users over time.

---

# 6. Then How Does Spring Know the Current User?

Every request carries the JWT.

Example:

```
Authorization: Bearer eyJhbGciOi...
```

For every request:

```
Request Arrives
      │
      ▼
Read JWT
      │
      ▼
Validate JWT
      │
      ▼
Create Authentication Object
      │
      ▼
Store Authentication
      │
      ▼
Controller Executes
```

Spring **recreates** the Authentication object every request.

Nothing is remembered from previous requests.

---

# 7. SecurityContextHolder

Many beginners imagine this:

```
Global Authentication

Authentication
      │
      └── Alice
```

This would be impossible because multiple users access the server simultaneously.

Instead,

Spring uses **ThreadLocal**.

---

# 8. What is ThreadLocal?

Think of ThreadLocal as giving every thread its own private locker.

```
Thread-1

Stack

Locker
└── Authentication → Alice


Thread-2

Stack

Locker
└── Authentication → Bob


Thread-3

Stack

Locker
└── Empty
```

When code executes

```java
SecurityContextHolder.getContext()
```

Spring actually retrieves

```
Current Thread
      │
      ▼
ThreadLocal
      │
      ▼
This Thread's SecurityContext
```

So,

If Thread-1 is executing,

```
SecurityContextHolder.getContext()

↓

Alice Authentication
```

If Thread-2 is executing,

```
SecurityContextHolder.getContext()

↓

Bob Authentication
```

There is **no shared Authentication object**.

---

# 9. End of Request

After the controller finishes,

Spring Security clears the ThreadLocal.

```
Thread-1

Locker

Empty
```

The thread returns to the pool.

Later it may process another user's request.

---

# 10. Why Check for Null?

JWT filters usually contain:

```java
if (username != null &&
    SecurityContextHolder.getContext().getAuthentication() == null) {

    // Authenticate user
}
```

This does **NOT** ask:

> "Is any user authenticated?"

Instead it asks:

> "Has THIS request already been authenticated?"

Because the SecurityContext belongs only to the **current thread**.

At the beginning of a request,

```
Thread-7

Locker

Empty
```

After JWT verification,

```
Thread-7

Locker

Authentication → Alice
```

At the end,

```
Thread-7

Locker

Empty
```

---

# Complete Lifecycle

```
Start Spring Boot
        │
        ▼
One Process
        │
        ▼
Tomcat Creates Thread Pool
        │
        ▼
Request Arrives
        │
        ▼
Idle Thread Selected
        │
        ▼
Read JWT
        │
        ▼
Validate JWT
        │
        ▼
Create Authentication
        │
        ▼
Store Authentication in ThreadLocal
        │
        ▼
Controller Executes
        │
        ▼
Response Sent
        │
        ▼
SecurityContext Cleared
        │
        ▼
Thread Returns to Pool
```

---

# Key Takeaways

### Process

- One Spring Boot application = One OS Process.
- One PCB per process.

### Threads

- Tomcat creates a thread pool.
- Threads are reused.
- Threads are **not** tied to users.

### Memory

Shared by all threads:
- Heap
- Text Segment
- Static Variables

Private to each thread:
- Stack
- Program Counter
- Registers
- ThreadLocal Variables

### Authentication

- JWT carries user identity.
- Every request recreates the Authentication object.
- Authentication is stored in ThreadLocal.
- SecurityContext is cleared after every request.

### SecurityContextHolder

`SecurityContextHolder` is **not** a global authentication store.

It is simply a wrapper around a `ThreadLocal<SecurityContext>`.

That is why:

```java
SecurityContextHolder.getContext().getAuthentication()
```

always returns **the Authentication object belonging to the currently executing request**, even though thousands of users may be using the application simultaneously.