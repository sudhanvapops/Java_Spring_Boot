package com.sudhanva.library_management_v2.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sudhanva.library_management_v2.Model.BorrowTransaction;

@Repository
public interface BorrowTransactionRepo extends JpaRepository<BorrowTransaction,Long>{
    
}
