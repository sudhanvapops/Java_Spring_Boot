### @SpringBootTest

JUnit by itself knows nothing about Spring.

If you want Spring to create your:
    @Service
    @Repository
    @Controller
    database configuration
    dependency injection
    etc.

use @SpringBootTest at class level

JUnit
  ↓
@SpringBootTest
  ↓
Start Spring ApplicationContext
  ↓
Create beans
  ↓
Dependency Injection
  ↓
BookService
  ↓
BookRepository
  ↓
Database

@SpringBootTest = Give my test a real Spring environment

