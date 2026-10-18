# Graph Report - library-management-v2  (2026-10-18)

## Corpus Check
- 15 files · ~18,884 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 810 nodes · 2583 edges · 27 communities (14 shown, 13 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 102 edges (avg confidence: 0.86)
- Token cost: 1,800 input · 950 output

## Community Hubs (Navigation)
- HTTP & Security Exceptions
- JPA Entities and Auth
- Integration Test Infrastructure
- Core Java Utilities
- OpenAPI Swagger Layer
- Book Controller & Transactions
- Bean Validation Constraints
- Error Code Definitions
- Borrow Transaction Controller
- Security & Spring Config
- Member Controller Layer
- Concepts & DTO Patterns
- Filter Chain & Exception Handling
- Logging & Profiles
- DataSource & Perf Profile Config
- Spring Boot Application Entry
- Error Enum & Global Handler
- JWT Token Configuration
- Spring Boot REST API
- Spring Data JPA Methods
- Lazy vs Eager Loading
- AOP Aspect Programming
- Optimistic Locking
- Pagination Support
- Spring Cache
- Project Root Artifact

## God Nodes (most connected - your core abstractions)
1. `ErrorResponseDto` - 100 edges
2. `ApiResponse` - 76 edges
3. `ErrorCode` - 61 edges
4. `SettingKey` - 31 edges
5. `UserRoles` - 28 edges
6. `User` - 25 edges
7. `UserPrincipal` - 24 edges
8. `BookResponse` - 23 edges
9. `AuthService` - 23 edges
10. `MemberResponse` - 22 edges

## Surprising Connections (you probably didn't know these)
- `accessToken (JWT Bearer Token)` --semantically_similar_to--> `Access Token`  [INFERRED] [semantically similar]
  src/main/java/com/sudhanva/library_management_v2/Model/Dto/Auth/login/notes.md → TODO.md
- `Borrow Workflow` --conceptually_related_to--> `@Transactional`  [INFERRED]
  Readme.md → src/cheetSheet.md
- `Role-Based Authorization` --conceptually_related_to--> `Admin User Management Endpoints`  [INFERRED]
  TODO.md → src/main/java/com/sudhanva/library_management_v2/security/Auth/notes.md
- `Spring Security with JWT Authentication` --semantically_similar_to--> `Spring Security + JWT (Planned)`  [INFERRED] [semantically similar]
  TODO.md → Readme.md
- `Spring DataSource (PostgreSQL, perf profile)` --semantically_similar_to--> `Spring DataSource (PostgreSQL)`  [INFERRED] [semantically similar]
  src/test/resources/application-perf.yml → src/main/resources/application.yml

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Borrow, Return, and Fine Calculation Workflow** — library_management_v2_readme_borrow_workflow, library_management_v2_readme_return_workflow, library_management_v2_readme_fine_calculation [EXTRACTED 1.00]
- **JWT Authentication Token Lifecycle** — library_management_v2_todo_access_token, library_management_v2_todo_refresh_token, library_management_v2_todo_refresh_access_token [INFERRED 0.85]
- **Perf Profile Observability Cluster (SQL logging + Hibernate stats + Actuator metrics)** — src_test_resources_application_perf_yml_jpa_hibernate, src_test_resources_application_perf_yml_actuator_endpoints, concept_spring_profile_perf [INFERRED 0.85]
- **Autocommit Disabled + Provider Disables Autocommit + HikariCP — Transaction Management Triad** — src_main_resources_application_yml_hikari_pool, src_main_resources_application_yml_jpa_hibernate, concept_autocommit_transaction_management [EXTRACTED 1.00]
- **Shared PostgreSQL DataSource — Flyway + JPA/Hibernate Integration** — src_main_resources_application_yml_spring_datasource, src_main_resources_application_yml_flyway, src_main_resources_application_yml_jpa_hibernate [INFERRED 0.95]

## Communities (27 total, 13 thin omitted)

### Community 0 - "HTTP & Security Exceptions"
Cohesion: 0.05
Nodes (23): NoRefreshTokenExistsException, NoRefreshTokenRecordExists, NotRefreshTokenException, TokenExpiredException, TokenRevokedException, TokenSubjectMismatchException, InvalidReturnDateException, NoActiveBorrowedBooksException (+15 more)

### Community 1 - "JPA Entities and Auth"
Cohesion: 0.06
Nodes (28): UserRoles, ADMIN, LIBRARIAN, MEMBER, InvalidStaffRoleException, Book, BorrowRecord, BorrowTransaction (+20 more)

### Community 2 - "Integration Test Infrastructure"
Cohesion: 0.05
Nodes (7): BookNotBorrowedByMemberException, BorrowRecordRepo, BorrowRecordService, BorrowTransactionServiceIntegrationTest, ConcurencyTest, IsbnTest, PerfContainerConfig

### Community 3 - "Core Java Utilities"
Cohesion: 0.05
Nodes (9): UserNotFoundException, LogoutResponseDto, RefreshTokenRepo, AuthService, JwtFilter, JwtService, RefreshTokenService, UserPrincipal (+1 more)

### Community 4 - "OpenAPI Swagger Layer"
Cohesion: 0.10
Nodes (8): BorrowRecordController, LoginRequestDto, RegisterRequestDto, BorrowTransactionItemResponse, DueTodayResponse, AuthController, AdminOnly, StaffOnly

### Community 5 - "Book Controller & Transactions"
Cohesion: 0.09
Nodes (13): BookController, BookAlreadyActiveException, BookAlreadyExistsException, BookCurrentlyBorrowedException, BookInactiveException, BookIsbnAlreadyExistsException, BookNotFoundException, InvalidBookCopiesException (+5 more)

### Community 6 - "Bean Validation Constraints"
Cohesion: 0.08
Nodes (18): LibrarySettingsController, SettingKey, FINE_PER_DAY, MAX_BOOKS, MAX_BORROW_DAYS, SettingValueType, BOOLEAN, DECIMAL (+10 more)

### Community 7 - "Error Code Definitions"
Cohesion: 0.05
Nodes (42): ErrorCode, BOOK_ALREADY_BORROWED_BY_MEMBER, BOOK_ALREADY_EXISTS, BOOK_CURRENTLY_BORROWED, BOOK_INACTIVE, BOOK_ISBN_ALREADY_EXISTS, BOOK_NOT_AVAILABLE, BOOK_NOT_BORROWED_BY_MEMBER (+34 more)

### Community 8 - "Borrow Transaction Controller"
Cohesion: 0.10
Nodes (9): BorrowTransactionController, BookNotAvailableException, DuplicateBookRequestException, BookAlreadyBorrowedByMemberException, BorrowTransactionNotFoundException, MaxBookLimitExceededException, NoBorrowTransactionsFoundException, BorrowTransactionResponse (+1 more)

### Community 9 - "Security & Spring Config"
Cohesion: 0.12
Nodes (3): DataSeeder, OpenApiConfig, SecurityConfig

### Community 10 - "Member Controller Layer"
Cohesion: 0.17
Nodes (8): MemberController, MemberHasActiveBorrowsException, MemberInactiveException, MemberNotFoundException, MemberRequest, MemberResponse, MemberRepo, MemberService

### Community 11 - "Concepts & DTO Patterns"
Cohesion: 0.09
Nodes (20): accessToken (JWT Bearer Token), Login Response DTO, Book Management, Borrow Workflow, Fine Calculation, Spring Security + JWT (Planned), Library Management System, Member Management (+12 more)

### Community 12 - "Filter Chain & Exception Handling"
Cohesion: 0.18
Nodes (3): JwtConfig, CustomAccessDeniedHandler, CustomAuthenticationEntryPoint

### Community 14 - "DataSource & Perf Profile Config"
Cohesion: 0.24
Nodes (9): Flyway Database Migration (main), HikariCP Connection Pool (main), JPA/Hibernate Configuration (main), Spring DataSource (PostgreSQL), Spring Boot Actuator Endpoints (perf profile), Flyway Database Migration (perf profile), HikariCP Connection Pool (perf profile), JPA/Hibernate Configuration (perf profile) (+1 more)

## Knowledge Gaps
- **71 isolated node(s):** `ADMIN`, `LIBRARIAN`, `MEMBER`, `FINE_PER_DAY`, `MAX_BOOKS` (+66 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 186 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **13 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ErrorResponseDto` connect `HTTP & Security Exceptions` to `JPA Entities and Auth`, `OpenAPI Swagger Layer`, `Book Controller & Transactions`, `Bean Validation Constraints`, `Error Code Definitions`, `Borrow Transaction Controller`, `Member Controller Layer`, `Filter Chain & Exception Handling`?**
  _High betweenness centrality (0.138) - this node is a cross-community bridge._
- **What connects `ADMIN`, `LIBRARIAN`, `MEMBER` to the rest of the system?**
  _71 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `HTTP & Security Exceptions` be split into smaller, more focused modules?**
  _Cohesion score 0.05292353823088456 - nodes in this community are weakly interconnected._
- **Why does `ErrorCode` connect `Error Code Definitions` to `HTTP & Security Exceptions`, `Filter Chain & Exception Handling`?**
  _High betweenness centrality (0.096) - this node is a cross-community bridge._
- **Should `JPA Entities and Auth` be split into smaller, more focused modules?**
  _Cohesion score 0.05801445953094692 - nodes in this community are weakly interconnected._
- **Why does `ApiResponse` connect `Book Controller & Transactions` to `JPA Entities and Auth`, `Core Java Utilities`, `OpenAPI Swagger Layer`, `Bean Validation Constraints`, `Borrow Transaction Controller`, `Member Controller Layer`?**
  _High betweenness centrality (0.067) - this node is a cross-community bridge._
- **Should `Integration Test Infrastructure` be split into smaller, more focused modules?**
  _Cohesion score 0.050940438871473356 - nodes in this community are weakly interconnected._