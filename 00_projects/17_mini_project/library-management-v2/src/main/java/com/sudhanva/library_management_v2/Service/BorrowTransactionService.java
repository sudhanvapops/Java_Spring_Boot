package com.sudhanva.library_management_v2.Service;

import org.springframework.stereotype.Service;

import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionResponse;
import com.sudhanva.library_management_v2.repo.BorrowRecordRepo;
import com.sudhanva.library_management_v2.repo.BorrowTransactionRepo;



@Service
public class BorrowTransactionService {

    final private BorrowTransactionRepo bTRepo;
    final private BorrowRecordRepo bRRepo;

    BorrowTransactionService(
        BorrowTransactionRepo bTRepo,
        BorrowRecordRepo bRRepo 
    ) {
        this.bTRepo = bTRepo;
        this.bRRepo = bRRepo;
    }


    public ApiResponse<BorrowTransactionResponse> borrowBook(){
        return null;
    }


    public ApiResponse<BorrowTransactionResponse> getBookById() {
       return null;
    }


}
