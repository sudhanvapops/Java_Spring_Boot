package com.sudhanva.library_management_v2.Service;

import com.sudhanva.library_management_v2.repo.BookRepo;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sudhanva.library_management_v2.Model.BorrowRecord;
import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionItemResponse;
import com.sudhanva.library_management_v2.repo.BorrowRecordRepo;
import com.sudhanva.library_management_v2.repo.MemberRepo;




@Service
public class BorrowRecordService {

    private final BookRepo bookRepo;
    final private BorrowRecordRepo borrowRecordRepo;
    final private MemberRepo memberRepo;

    public BorrowRecordService(
        BorrowRecordRepo borrowRecordRepo,
        MemberRepo memberRepo, BookRepo bookRepo
    ){
        this.borrowRecordRepo = borrowRecordRepo;
        this.memberRepo = memberRepo;
        this.bookRepo = bookRepo;
    }

    
    // Utlity Functions

    private BorrowTransactionItemResponse mapToBorrowTransactionItemResponse(
        BorrowRecord borrowRecord
    ){
        return BorrowTransactionItemResponse.builder()
            .bookName(borrowRecord.getBook().getName())
            .borrowedBookId(borrowRecord.getBook().getId())
            .author(borrowRecord.getBook().getAuthor())
            .dueDate(borrowRecord.getDueDate())
            .build();
    }


    // Service Methods


    // Get All Records
    @Transactional(readOnly = true)
    public ApiResponse<List<BorrowTransactionItemResponse>> getAllRecords(){

        
        List<BorrowRecord> response =  borrowRecordRepo.findAll();

        if (response.isEmpty()){
            return new ApiResponse<>(
                false,
                "No records found",
                null
            );
        }

        return new ApiResponse<>(
            true,
            "Records: "+ response.size(),
            response.stream().map( item -> mapToBorrowTransactionItemResponse(item)).toList()
        );
    }


    // Get All Records of a Member
    @Transactional(readOnly = true)
    public ApiResponse<List<BorrowTransactionItemResponse>> getAllMemberRecords(Long memberId){

        // Validate Member
        Member existedMember = memberRepo.findById(memberId).orElse(null);


        if(existedMember == null){
            return new ApiResponse<>(
                false,
                "Member doesn't exist, "+memberId,
                null
            );
        }

        
        // Reocrd Validation
        List<BorrowRecord> response =  borrowRecordRepo.findByBorrowTransactionMemberId(memberId);

        if (response.isEmpty()){
            return new ApiResponse<>(
                false,
                "No records found, Member Id: "+memberId,
                null
            );
        }

        return new ApiResponse<>(
            true,
            "Member Id: "+memberId+", Records: "+ response.size(),
            response.stream().map( item -> mapToBorrowTransactionItemResponse(item)).toList()
        );
    }


    // Get All Unreturned Records of a Member (return date null)
    @Transactional(readOnly = true)
    public ApiResponse<List<BorrowTransactionItemResponse>> getAllUnreturnedRecords(Long memberId){

        // validate member id
        Member existingMember = memberRepo.findById(memberId).orElse(null);

        if (existingMember == null){
            return new ApiResponse<>(
                false,
                "Member Doesn't exist: "+memberId,
                null
            );
        }


        // Find records
        List<BorrowRecord> borrowRecords = borrowRecordRepo.findByBorrowTransactionMemberIdAndReturnDateIsNull(memberId);

        if (borrowRecords.isEmpty()) {
            return new ApiResponse<>(
                false,
                "No Borrow Record Exist, Member Id: "+memberId,
                null
            );
        }


        List<BorrowTransactionItemResponse> borrowRecordsResponse = borrowRecords.stream()
                .map( record -> mapToBorrowTransactionItemResponse(record))
                .toList();

                
        return new ApiResponse<>(
            true,
            "Borrow Record Exist, Member Id "+memberId,
            borrowRecordsResponse
        );
    }


    // Get records due today

}