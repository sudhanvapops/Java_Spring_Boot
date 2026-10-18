### casuing the Exception

In My Model Which has @OneToMany

Entity	                    Collection field	                       
Member.java	                List<BorrowTransaction> borrowTransactions
BorrowTransaction.java	    List<BorrowRecord> records	               
Book.java	                List<BorrowRecord> borrowRecords	       


### Query 

fetch a member along with 
all their borrowing transactions and the books associated with each transaction
in one database query.

@Query("""
    SELECT DISTINCT m
    FROM Member m
    LEFT JOIN FETCH m.borrowTransactions bt
    LEFT JOIN FETCH bt.records
    WHERE m.id = :memberId
    """)
Optional<Member> findMemberWithTransactionsAndRecords(
    @Param("memberId") Long memberId
);

this assumes Member has an id field of type Long; adjust the field or parameter type if your entity differs.

this query is expected to throw MultipleBagFetchException when executed, because both borrowTransactions and records are List collections that Hibernate treats as bags.

org.hibernate.loader.MultipleBagFetchException:
cannot simultaneously fetch multiple bags


### Fixing 

Query 1: Fetch the member and their transactions
@Query("""
    SELECT DISTINCT m
    FROM Member m
    LEFT JOIN FETCH m.borrowTransactions
    WHERE m.id = :memberId
    """)
Optional<Member> findMemberWithTransactions(
    @Param("memberId") Long memberId
);


Query 2: Fetch the records for those transactions
@Query("""
    SELECT DISTINCT bt
    FROM BorrowTransaction bt
    LEFT JOIN FETCH bt.records
    WHERE bt.member.id = :memberId
    """)
List<BorrowTransaction> findTransactionsWithRecords(
    @Param("memberId") Long memberId
);

Call both methods within a service method annotated with @Transactional
