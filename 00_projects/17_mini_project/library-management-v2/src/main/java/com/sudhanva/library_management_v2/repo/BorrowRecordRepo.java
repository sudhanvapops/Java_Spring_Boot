package com.sudhanva.library_management_v2.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.sudhanva.library_management_v2.Model.BorrowRecord;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.DueTodayResponse;


@Repository
public interface BorrowRecordRepo extends JpaRepository<BorrowRecord,Long>{


    List<BorrowRecord> findByBorrowTransactionMemberIdAndReturnDateIsNull(Long id);


// BorrowRecord
//     ↓ borrowTransaction
// BorrowTransaction
//     ↓ member
// Member
//     ↓ id
// returnDate IS NULL

    List<BorrowRecord> findByBorrowTransactionMemberId(Long id);

    List<BorrowRecord> findByDueDateGreaterThanEqualAndDueDateLessThanAndReturnDateIsNull(
        LocalDateTime start,
        LocalDateTime end
    );  


    // For Performace we are writing the HQL
    // Also Returining DTO directly
    @Query("""
        SELECT new com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.DueTodayResponse(
            b.id,
            b.name,
            b.author,
            br.dueDate,
            m.id,
            m.name,
            m.email
        )
        FROM BorrowRecord br
        JOIN br.book b
        JOIN br.borrowTransaction bt
        JOIN bt.member m
        WHERE br.returnDate IS NULL
        AND br.dueDate >= :start
        AND br.dueDate < :end
    """)
    List<DueTodayResponse> findDueToday(
            LocalDateTime start,
            LocalDateTime end
    );


    @EntityGraph(attributePaths = {
        "borrowTransaction",
        "borrowTransaction.member",
        "book"
    })
    List<BorrowRecord> findByReturnDateIsNull();

}
