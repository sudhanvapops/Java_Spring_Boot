### Transaction Context

it represents the Database tranasction currently in porgress
are these operations part of the same transaction

SO think it as should these two operations
Succed or fail together

So for example: 

Audit logs
Recording failurs
Security logs
Rewcorign that the operation was attempted

all these require seprate Transactions

T1: borrowBook
    ↓
    T1 suspended
    ↓
T2: auditService.log()
    ↓
    T2 COMMIT ✅
    ↓
T1 resumes
    ↓
T1 ROLLBACK ❌

So the audit record survives the rollback.
