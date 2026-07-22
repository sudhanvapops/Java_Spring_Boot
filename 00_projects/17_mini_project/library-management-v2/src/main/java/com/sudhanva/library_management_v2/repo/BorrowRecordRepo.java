package com.sudhanva.library_management_v2.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sudhanva.library_management_v2.Model.BorrowRecord;


@Repository
public interface BorrowRecordRepo extends JpaRepository<BorrowRecord,Long>{
    List<BorrowRecord> findByBorrowTransactionMemberIdAndReturnDateIsNull(Long id);

    List<BorrowRecord> findByBorrowTransactionMemberId(Long id);

// BorrowRecord
//     ↓ borrowTransaction
// BorrowTransaction
//     ↓ member
// Member
//     ↓ id
// returnDate IS NULL
}
