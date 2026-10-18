# Graph Report - library-management-v2  (2026-10-15)

## Corpus Check
- Corpus is ~49,094 words - fits in a single context window. You may not need a graph.

## Summary
- 766 nodes · 2560 edges · 41 communities (20 shown, 21 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 103 edges (avg confidence: 0.85)
- Token cost: 4,200 input · 1,800 output

## Community Hubs (Navigation)
- API Response and Web Layer
- JPA Entities and Persistence
- Validation and Annotation Processing
- Spring Security and CORS Config
- Book Management and Transactions
- Error Codes and Enums
- JPA Repository and Locking
- Integration and Concurrency Tests
- Authentication Service
- JWT Exception Handling
- Borrow Record Controller
- Docs and Design Concepts
- Spring Security UserDetails
- Borrow Return Flow
- Global Exception Handler
- JWT Token Generation
- Borrow Transaction Controller
- Borrow Transaction Exceptions
- JWT Claims and Auth Exceptions
- Login and Refresh Flow
- Book Availability Exceptions
- Book Exception Handler
- Member Exception Handler
- Borrow Record Exceptions
- Spring Boot Test Setup
- Application Entry Point
- Token Expired Exception
- Token Revoked Exception
- Token Subject Mismatch
- Datasource and JPA Config
- Flyway Migration Config
- Error Enum Design Notes
- Library REST API Overview
- Lazy and Eager Loading Notes
- AOP Notes
- Optimistic Locking Notes
- Pagination Notes
- Spring Cache Notes
- Root Package

## God Nodes (most connected - your core abstractions)
1. `ErrorResponseDto` - 100 edges
2. `ApiResponse` - 85 edges
3. `ErrorCode` - 61 edges
4. `SettingKey` - 33 edges
5. `UserRoles` - 28 edges
6. `Book` - 27 edges
7. `User` - 25 edges
8. `BookRepo` - 24 edges
9. `UserPrincipal` - 24 edges
10. `BookResponse` - 23 edges

## Surprising Connections (you probably didn't know these)
- `accessToken (JWT Bearer Token)` --semantically_similar_to--> `Access Token`  [INFERRED] [semantically similar]
  src/main/java/com/sudhanva/library_management_v2/Model/Dto/Auth/login/notes.md → TODO.md
- `Borrow Workflow` --conceptually_related_to--> `@Transactional`  [INFERRED]
  Readme.md → src/cheetSheet.md
- `JWT Configuration` --conceptually_related_to--> `Access Token`  [INFERRED]
  src/main/resources/application.yml → TODO.md
- `JWT Configuration` --conceptually_related_to--> `Refresh Token`  [INFERRED]
  src/main/resources/application.yml → TODO.md
- `Role-Based Authorization` --conceptually_related_to--> `Admin User Management Endpoints`  [INFERRED]
  TODO.md → src/main/java/com/sudhanva/library_management_v2/security/Auth/notes.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **JWT Authentication Token Lifecycle** — library_management_v2_todo_access_token, library_management_v2_todo_refresh_token, library_management_v2_todo_refresh_access_token, library_management_v2_src_main_resources_application_yml_jwt_config [INFERRED 0.85]
- **Borrow, Return, and Fine Calculation Workflow** — library_management_v2_readme_borrow_workflow, library_management_v2_readme_return_workflow, library_management_v2_readme_fine_calculation [EXTRACTED 1.00]
- **Schema Management via Flyway + JPA Validation** — library_management_v2_src_main_resources_application_yml_flyway, library_management_v2_src_main_resources_application_yml_jpa_validate, library_management_v2_src_main_resources_application_yml_datasource [INFERRED 0.85]

## Communities (41 total, 21 thin omitted)

### Community 0 - "API Response and Web Layer"
Cohesion: 0.09
Nodes (13): BookController, BorrowTransactionController, MemberController, MemberInactiveException, MemberNotFoundException, ApiResponse, LoginRequestDto, RegisterRequestDto (+5 more)

### Community 1 - "JPA Entities and Persistence"
Cohesion: 0.10
Nodes (17): JwtConfig, UserRoles, ADMIN, LIBRARIAN, MEMBER, Book, BorrowRecord, BorrowTransaction (+9 more)

### Community 2 - "Validation and Annotation Processing"
Cohesion: 0.07
Nodes (19): LibrarySettingsController, SettingKey, FINE_PER_DAY, MAX_BOOKS, MAX_BORROW_DAYS, SettingValueType, BOOLEAN, DECIMAL (+11 more)

### Community 3 - "Spring Security and CORS Config"
Cohesion: 0.07
Nodes (6): DataSeeder, OpenApiConfig, SecurityConfig, CustomAccessDeniedHandler, CustomAuthenticationEntryPoint, JwtFilter

### Community 4 - "Book Management and Transactions"
Cohesion: 0.09
Nodes (11): BookAlreadyActiveException, BookAlreadyExistsException, BookCurrentlyBorrowedException, BookInactiveException, BookIsbnAlreadyExistsException, BookNotFoundException, InvalidBookCopiesException, NoBooksFoundException (+3 more)

### Community 5 - "Error Codes and Enums"
Cohesion: 0.05
Nodes (42): ErrorCode, BOOK_ALREADY_BORROWED_BY_MEMBER, BOOK_ALREADY_EXISTS, BOOK_CURRENTLY_BORROWED, BOOK_INACTIVE, BOOK_ISBN_ALREADY_EXISTS, BOOK_NOT_AVAILABLE, BOOK_NOT_BORROWED_BY_MEMBER (+34 more)

### Community 6 - "JPA Repository and Locking"
Cohesion: 0.11
Nodes (5): BookRepo, BorrowRecordRepo, BorrowTransactionRepo, LibrarySettingsRepo, MemberRepo

### Community 7 - "Integration and Concurrency Tests"
Cohesion: 0.13
Nodes (5): BorrowTransactionItemRequest, BorrowTransactionRequest, BorrowTransactionServiceIntegrationTest, ConcurencyTest, IsbnTest

### Community 8 - "Authentication Service"
Cohesion: 0.13
Nodes (8): InvalidStaffRoleException, PasswordAndConfirmPasswordDoesntMatchException, UsernameAlreadyExistsException, RegisterResponseDto, StaffRegisterRequestDto, RefreshTokenRepo, AuthService, RefreshTokenService

### Community 9 - "JWT Exception Handling"
Cohesion: 0.18
Nodes (3): NoRefreshTokenExistsException, AuthExcptionHandlers, UserEmailAlreadyExistsException

### Community 10 - "Borrow Record Controller"
Cohesion: 0.16
Nodes (6): BorrowRecordController, NoBorrowRecordsFoundException, NoUnreturnedBooksFoundException, BorrowTransactionItemResponse, DueTodayResponse, BorrowRecordService

### Community 11 - "Docs and Design Concepts"
Cohesion: 0.09
Nodes (21): accessToken (JWT Bearer Token), Login Response DTO, Book Management, Borrow Workflow, Fine Calculation, Spring Security + JWT (Planned), Library Management System, Member Management (+13 more)

### Community 12 - "Spring Security UserDetails"
Cohesion: 0.13
Nodes (3): UserNotFoundException, UserRepo, CustomUserDetailsService

### Community 13 - "Borrow Return Flow"
Cohesion: 0.13
Nodes (5): DuplicateBookRequestException, BookNotBorrowedByMemberException, BookReturnRequest, BookReturnResponse, BorrowReturnItemRequest

### Community 14 - "Global Exception Handler"
Cohesion: 0.22
Nodes (3): InvalidReturnDateException, BorrowExceptionHandler, GlobalExceptionHandler

### Community 16 - "Borrow Transaction Controller"
Cohesion: 0.23
Nodes (4): BorrowTransactionNotFoundException, NoBorrowTransactionsFoundException, BorrowTransactionResponse, BorrowTransactionService

### Community 17 - "Borrow Transaction Exceptions"
Cohesion: 0.34
Nodes (3): BorrowTransactionExceptionHandler, LibrarySettingsExceptionHandler, ErrorResponseDto

### Community 18 - "JWT Claims and Auth Exceptions"
Cohesion: 0.18
Nodes (3): NoRefreshTokenRecordExists, NotRefreshTokenException, LogoutResponseDto

### Community 20 - "Book Availability Exceptions"
Cohesion: 0.17
Nodes (3): BookNotAvailableException, BookAlreadyBorrowedByMemberException, MaxBookLimitExceededException

### Community 22 - "Member Exception Handler"
Cohesion: 0.25
Nodes (3): MemberExceptioHandler, MemberEmailAlreadyExistsException, MemberHasActiveBorrowsException

## Knowledge Gaps
- **69 isolated node(s):** `com.sudhanva:library-management-v2`, `MEMBER_NOT_FOUND`, `MEMBER_INACTIVE`, `MEMBER_EMAIL_ALREADY_EXISTS`, `MEMBER_HAS_ACTIVE_BORROWS` (+64 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 184 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **21 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ErrorResponseDto` connect `Borrow Transaction Exceptions` to `API Response and Web Layer`, `JPA Entities and Persistence`, `Validation and Annotation Processing`, `Spring Security and CORS Config`, `Error Codes and Enums`, `JWT Exception Handling`, `Borrow Record Controller`, `Borrow Return Flow`, `Global Exception Handler`, `Book Exception Handler`, `Member Exception Handler`, `Borrow Record Exceptions`?**
  _High betweenness centrality (0.136) - this node is a cross-community bridge._
- **What connects `com.sudhanva:library-management-v2`, `MEMBER_NOT_FOUND`, `MEMBER_INACTIVE` to the rest of the system?**
  _69 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `API Response and Web Layer` be split into smaller, more focused modules?**
  _Cohesion score 0.08646812957157785 - nodes in this community are weakly interconnected._
- **Why does `ApiResponse` connect `API Response and Web Layer` to `JPA Entities and Persistence`, `Validation and Annotation Processing`, `Book Management and Transactions`, `Integration and Concurrency Tests`, `Authentication Service`, `Borrow Record Controller`, `Borrow Return Flow`, `Borrow Transaction Controller`, `JWT Claims and Auth Exceptions`, `Login and Refresh Flow`, `Book Availability Exceptions`?**
  _High betweenness centrality (0.104) - this node is a cross-community bridge._
- **Should `JPA Entities and Persistence` be split into smaller, more focused modules?**
  _Cohesion score 0.10052677787532924 - nodes in this community are weakly interconnected._
- **Why does `ErrorCode` connect `Error Codes and Enums` to `Spring Security and CORS Config`, `JWT Exception Handling`, `Global Exception Handler`, `Borrow Transaction Exceptions`, `Book Exception Handler`, `Member Exception Handler`, `Borrow Record Exceptions`?**
  _High betweenness centrality (0.102) - this node is a cross-community bridge._
- **Should `Validation and Annotation Processing` be split into smaller, more focused modules?**
  _Cohesion score 0.07111501316944688 - nodes in this community are weakly interconnected._