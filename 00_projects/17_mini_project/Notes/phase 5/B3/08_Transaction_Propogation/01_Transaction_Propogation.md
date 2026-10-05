### Transaction Propogation

So it means how a transaction is propgated to another method which might or moght not be a transcaction and how to handle it when called

So What should happen to that other transaction

When one transactional method calls another method, what should happen to the transaction

or also when a non transaction method is called another transaction method


Spring is deciding what to do with the current transaction context when another @Transactional method is entered.


So Depending on what we want 
we can give 
Types of Transaction Propogation types
to get the Job Done

That is:
    Join the existing transaction
    Create a new transaction
    Run without a transaction
    Fail if a transaction doesn't exist, etc.


### Required

Its a Default Propogation Type

what it will do
If calling method(expl: contolerr method) dotn have a transaction
it will start a new transaction by itself

or if an ongoing transaction is there
then it will join to that same transaction


A starts Transaction T1
        ↓
B joins T1
        ↓
B finishes
        ↓
A finishes → T1 commits


You can have multiple methods and still have one transaction
Method boundries != Transactional Boundries



### Required_New

Its says i want my own idependent Transaction
so even if a transaction already exists
It will create its own transaction T2

T1 wil get suspended
T2 will get Commited / Roll back
T1 gets resumed


log uses REQUIRED_NEW



So there are independent outcomes
Why we reuiwre diffrent transaction
when we want each to succed or get failed difrently

so think it as should these two operations should succed or fail together or not


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


Suspending meaning

T2 is not created inside T1

T1
│
└── suspended

T2
│
└── log()

Then

T2
│
└── COMMIT

T1
│
└── RESUMES


### Summary

REQUIRED: Transaction OwnerShip follows the outer call chain
REQUIRED_NEW: A method can deliberately create a separate transaction boundary inside the call chain.

But: A single Java method call chain can contain multiple transaction boundaries.



|                                    | `REQUIRED`               | `REQUIRES_NEW` |
| ---------------------------------- | ------------------------ | -------------- |
| Existing transaction?              | **Join it**              | **Suspend it** |
| New transaction?                   | Only if none exists      | **Always**     |
| Failure affects outer transaction? | Yes                      | Independent    |
| Typical use                        | Main business operations | Audit/logging  |



### Why Audit Logging is Good Example


I want a record in DB

"Memeber 42 Attempted to Borrow book"

if borrow failed
"Member 42 failed to borrow book"

if we didnt used REQUIRED_NEW
The Attempted log will also get rolled back


