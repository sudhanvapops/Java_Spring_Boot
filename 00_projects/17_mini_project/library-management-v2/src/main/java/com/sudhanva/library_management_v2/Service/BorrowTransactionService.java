package com.sudhanva.library_management_v2.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sudhanva.library_management_v2.Model.BorrowRecord;
import com.sudhanva.library_management_v2.Model.BorrowTransaction;
import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionItemResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionResponse;
import com.sudhanva.library_management_v2.repo.BorrowRecordRepo;
import com.sudhanva.library_management_v2.repo.BorrowTransactionRepo;
import com.sudhanva.library_management_v2.repo.MemberRepo;



@Service
public class BorrowTransactionService {

    final private BorrowTransactionRepo bTRepo;
    final private BorrowRecordRepo bRRepo;
    final private MemberRepo memberRepo;

    BorrowTransactionService(
        BorrowTransactionRepo bTRepo,
        BorrowRecordRepo bRRepo, 
        MemberRepo memberRepo 
    ) {
        this.bTRepo = bTRepo;
        this.bRRepo = bRRepo;
        this.memberRepo = memberRepo;
    }



    // Utility methods

    private List<BorrowTransactionItemResponse> mapToBorrowTransactionItemResponse(
        List<BorrowRecord> brList
    ){

        List<BorrowTransactionItemResponse> borrowRecordList = 
            new ArrayList<>();

        for (BorrowRecord borrowRecord : brList) {

            borrowRecordList.add(
                BorrowTransactionItemResponse.builder()
                .borrowedBookId(borrowRecord.getBook().getId())
                .bookName(borrowRecord.getBook().getName())
                .author(borrowRecord.getBook().getAuthor())
                .dueDate(borrowRecord.getDueDate())
                .build()
            );
        }
        
        return borrowRecordList;
    }


    private BorrowTransactionResponse mapToBorrowTransactionResponse(
        BorrowTransaction bt
    ){

        return BorrowTransactionResponse.builder()
        .transactionId(bt.getId()) 
        .memberName(bt.getMember().getName())
        .books(mapToBorrowTransactionItemResponse(bt.getRecords()))
        .borrowDate(bt.getBorrowDate())
        .build();
        
    }

    // service methods


    @Transactional(readOnly = true)
    public ApiResponse<BorrowTransactionResponse> getTransactionById(Long id) {

        BorrowTransaction borrowTransaction =  
            bTRepo.findById(id).orElse(null);

        if (borrowTransaction == null){
            return new ApiResponse<>(
                false,
                "No transaction found with id: "+id,
                null
            );
        }

        BorrowTransactionResponse response = 
            mapToBorrowTransactionResponse(borrowTransaction);

        return new ApiResponse<>(
            true,
            "found transaction with id: "+id,
            response
        );

    }



    // Get all Trnsaction
    @Transactional(readOnly = true)
    public ApiResponse<List<BorrowTransactionResponse>> getAllTransaction(){

        List<BorrowTransaction> borrowTransactionsList =  bTRepo.findAll();

        if(borrowTransactionsList.isEmpty()){
            return new ApiResponse<>(
                false,
                "No transaction found",
                null
            );
        }

        List<BorrowTransactionResponse> responses;

        responses = borrowTransactionsList.stream()
            .map(transaction -> mapToBorrowTransactionResponse(transaction))
            .toList();

        return new ApiResponse<>(
            true,
            "Transactions Found: "+borrowTransactionsList.size(),
            responses
        );

    }   


    // Get Transaction of a Particular Person
    @Transactional(readOnly = true)
    public ApiResponse<List<BorrowTransactionResponse>> getAllTransactionByMemeberId(Long memberId){

        Member existingMember = memberRepo.findById(memberId).orElse(null);

        if (existingMember == null){
            return new ApiResponse<>(
                false,
                "No Member found",
                null
            );
        }

        List<BorrowTransaction> borrowTransactionsList =  bTRepo.findByMemberId(memberId);

        if(borrowTransactionsList.isEmpty()){
            return new ApiResponse<>(
                false,
                "No Transaction found",
                null
            );
        }

        List<BorrowTransactionResponse> responses;

        responses = borrowTransactionsList.stream()
            .map(transaction -> mapToBorrowTransactionResponse(transaction))
            .toList();

        return new ApiResponse<>(
            true,
            "Transactions Found: "+borrowTransactionsList.size(),
            responses
        );

    }




}
