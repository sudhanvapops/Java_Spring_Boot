Why keep both totalCopies & availableCopies?
beacuse to know calculate how many borrowed 

Can availableCopies go negative? how prevent?
no 
prevention in both layers


Add id startergy for auto id
Add deafult values

MappedBy to ManyToMany
// the field name that owns the relationship in the other entity
also add Join Table
    
# 📚 Library Management System – Book Entity (Learning Notes)

## 🎯 Purpose

This document summarizes the **mistakes, misconceptions, and corrections** I made while designing the `Book` entity using **Java + Hibernate**.

---

# ❌ Mistakes I Made

## 1. Wrong Many-to-Many Mapping

```java
@ManyToMany
private Set<String> authors;
```

### ❌ Problem:

* Used `String` instead of an entity
* Hibernate relationships work only between **entities**

### ✅ Fix:

```java
private Set<Author> authors;
```

---

## 2. Incorrect use of `mappedBy`

```java
@ManyToMany(mappedBy = "authorName")
```

### ❌ Problem:

* `mappedBy` must refer to a **field name in the other entity**
* `authorName` is not a relationship field

### ✅ Fix:

* Either:

  * Remove `mappedBy` and make `Book` the **owner**
* Or:

  * Use correct field name from `Author` entity (e.g., `books`)

---

## 3. Treating Relationship as a Simple Field

```java
private String author;
```

### ❌ Problem:

* Ignored that:

  * One Book → Many Authors
  * One Author → Many Books

### ✅ Fix:

* Use **Many-to-Many relationship**

---

## 4. Ignoring Business Constraints Initially

### ❌ Problem:

* Did not enforce:

```text
availableCopies <= totalCopies
```

### ✅ Fix:

* Add **DB-level CHECK constraint**
* Add **service-layer validation**

---

## 5. Misunderstanding Default Values

```java
@ColumnDefault("0")
```

### ❌ Problem:

* Thought it guarantees runtime default
* It only affects schema generation

### ✅ Fix:

* Use constructor:

```java
availableCopies = totalCopies;
```

---

## 6. Weak Naming Decisions

```java
private String bookName;
```

### ❌ Problem:

* Redundant naming

### ✅ Fix:

```java
private String title;
```

---

## 7. Not Initializing Collections

```java
private Set<Author> authors;
```

### ❌ Problem:

* Can cause `NullPointerException`

### ✅ Fix:

```java
private Set<Author> authors = new HashSet<>();
```

---

## 8. Not Thinking About Object Valid State

### ❌ Problem:

Objects could exist like:

```text
totalCopies = 5
availableCopies = 10
```

### ✅ Fix:

* Enforce:

  * Constructor logic
  * Service validation
  * DB constraint

---

## 9. Confusion About BorrowRecord Design

### ❌ Problem:

* Thought each borrower has their own table

### ✅ Fix:

* Single table:

```text
BorrowRecord
- borrower_id
- book_id
- borrow_date
```

---

## 10. Underestimating ISBN

### ❌ Problem:

* Thought ISBN is unnecessary

### ✅ Fix:

* ISBN is:

  * Globally unique
  * Business identifier
  * Must be `unique + not null`

---

# ✅ What I Learned

## 🧠 Core Concepts

* Entities represent **real-world data**
* Relationships are **first-class citizens**
* Constraints ensure **data integrity**
* Hibernate maps **objects ↔ tables**

---

## 🔗 Relationship Understanding

| Relationship            | Type         |
| ----------------------- | ------------ |
| Book ↔ Author           | Many-to-Many |
| Borrower → BorrowRecord | One-to-Many  |
| Book → BorrowRecord     | One-to-Many  |

---

## 🧱 Design Principles

* Don’t store events as fields → create entities (BorrowRecord)
* Separate **data** and **behavior**
* Always think in terms of:

  * **consistency**
  * **scalability**
  * **real-world modeling**

---

## ⚙️ Technical Learnings

* `@Entity` → marks class as DB table
* `@Id` + `@GeneratedValue` → primary key
* `@Column` → column constraints
* `@ManyToMany` → relationships
* `@JoinTable` → join table control
* `@Check` / SQL → complex constraints

---

# 🚀 Final Understanding

The `Book` entity is:

> A representation of a **type of book**, not individual copies

It contains:

* identity (id, isbn)
* metadata (title)
* relationships (authors)
* inventory (copies)

---

# 🔥 Key Mindset Shift

❌ Before:

> “I am writing a Java class”

✅ Now:

> “I am designing a system that maps real-world logic into a database”

---

# 🧭 Next Steps

* Implement `Author` entity (inverse side)
* Build `BorrowRecord`
* Add service-layer validations
* Test constraints with real DB operations

---

## 💡 Final Note

Mistakes were not failures — they were:

> **design decisions that got refined through reasoning**

---

✔️ This is how backend engineers are built.
