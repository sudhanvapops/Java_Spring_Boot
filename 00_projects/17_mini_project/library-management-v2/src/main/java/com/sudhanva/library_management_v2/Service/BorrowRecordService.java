package com.sudhanva.library_management_v2.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sudhanva.library_management_v2.Model.BorrowRecord;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionItemResponse;
import com.sudhanva.library_management_v2.repo.BorrowRecordRepo;




@Service
public class BorrowRecordService {


    final private BorrowRecordRepo borrowRecordRepo;

    public BorrowRecordService(
        BorrowRecordRepo borrowRecordRepo
    ){
        this.borrowRecordRepo = borrowRecordRepo;
    }
    

    // Get All Records
    public ApiResponse<BorrowTransactionItemResponse> getAllRecords(){
        return null;
    }


    // Get All Records of a Member



    // Get All Unreturned Records of a Member (return date null)
    public ApiResponse<BorrowTransactionItemResponse> getAllUnreturnedRecords(Long memberId){

        List<BorrowRecord> borrowRecords = borrowRecordRepo.findByBorrowTransactionMemberIdAndReturnDateIsNull(memberId);

        // validate member id


        return null;
    }


}
