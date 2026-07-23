<div align="center">

# 📚 Library Management System

### A RESTful Backend built with **Spring Boot** & **PostgreSQL**

Designing a real-world library workflow with borrowing, returning, validations, transactions and fine calculation.

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger)

</div>

---

# 🚀 Overview

This project is a **Spring Boot REST API** that simulates the backend of a Library Management System.

Instead of focusing on CRUD operations alone, the project models actual library workflows including:

- Borrowing multiple books
- Returning books
- Automatic fine calculation
- Book availability tracking
- Member validation
- Business rule enforcement
- Transaction management

---

# ✨ Features

## 👤 Member Management

- Create Member
- Update Member
- Get Member
- List Members
- Soft Delete
- Reactivate Member

---

## 📖 Book Management

- Add Book
- Update Book
- Search by Name
- Search by Author
- Get by ID
- List Books
- Soft Delete
- Reactivate Book

---

## 🔄 Borrow Workflow

- Borrow multiple books
- Prevent duplicate requests
- Prevent borrowing inactive books
- Prevent borrowing inactive members
- Prevent borrowing unavailable books
- Prevent borrowing the same active book twice
- Maximum borrowing limit validation

---

## 📥 Return Workflow

- Return one or multiple books
- Automatic fine calculation
- Update available copies
- Calculate total fine
- Return summary response

---

# 🏗️ Architecture

```text
                 Client
        (Swagger / Postman)

                 │
                 ▼

          REST Controllers

                 │
                 ▼

          Service Layer
    Business Rules & Validation

                 │
                 ▼

      Spring Data JPA Repository

                 │
                 ▼

          PostgreSQL Database
```

---

# 🗄️ Database Design

> Add your ER Diagram here

```md
![ER Diagram](docs/images/database.png)
```

---

# 🔄 Borrow Workflow

```text
Borrow Request

      │

      ▼

Validate Member

      │

      ▼

Validate Books

      │

      ▼

Business Rules

✔ Active Member
✔ Active Book
✔ Available Copies
✔ Maximum Borrow Limit
✔ Duplicate Prevention

      │

      ▼

Create Borrow Transaction

      │

      ▼

Create Borrow Records

      │

      ▼

Decrease Available Copies

      │

      ▼

Return Response
```

---

# 📦 Tech Stack

| Technology | Purpose |
|------------|---------|
| Java | Programming Language |
| Spring Boot | Backend Framework |
| Spring MVC | REST APIs |
| Spring Data JPA | ORM |
| PostgreSQL | Database |
| Hibernate | ORM Provider |
| Swagger / OpenAPI | API Documentation |
| Maven | Dependency Management |
| Postman | API Testing |

---

# 📚 REST APIs

## Member

- Create Member
- Update Member
- Delete Member
- Activate Member
- Get Member
- Get All Members

---

## Books

- Create Book
- Update Book
- Delete Book
- Activate Book
- Search by Name
- Search by Author
- Get Book
- Get All Books

---

## Borrow

- Borrow Books
- Get Transaction
- Get Member Transactions
- Get All Transactions

---

## Borrow Records

- Return Books
- Get Active Borrowed Books
- Due Today
- Get All Borrow Records

---

# 📸 API Documentation

Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI Docs

```text
http://localhost:8080/v3/api-docs
```

---

# 🧪 Testing

Every endpoint has been tested using **Postman**.

✔ Success Responses

✔ Validation Errors

✔ Business Rule Failures

✔ Duplicate Requests

✔ Fine Calculation

✔ Borrow & Return Workflow

---

# 📁 Project Structure

```text
src
 ├── controller
 ├── dto
 ├── entity
 ├── repository
 ├── service
 ├── mapper
 ├── config
 └── validation
```

---

# ⚙️ Running Locally

```bash
git clone https://github.com/yourusername/library-management-system.git
```

```bash
cd library-management-system
```

Configure

```properties
application.properties
```

```properties
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
```

Run

```bash
mvn spring-boot:run
```

---

# 📈 Future Improvements

- Global Exception Handling
- Spring Security + JWT
- Role Based Authorization
- Pagination & Sorting
- Unit Testing
- Integration Testing
- Docker
- Redis
- Deployment

---

# 📖 What I Learned

Throughout this project I gained hands-on experience with:

- Spring Boot
- Spring MVC
- Spring Data JPA
- Entity Relationships
- REST API Design
- DTO Pattern
- Validation
- Transaction Management
- Swagger Documentation
- Backend Business Logic

---

<div align="center">

### ⭐ If you found this project interesting, consider giving it a star!

</div>