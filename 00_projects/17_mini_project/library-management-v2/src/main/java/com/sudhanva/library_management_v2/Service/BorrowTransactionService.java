package com.sudhanva.library_management_v2.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sudhanva.library_management_v2.Model.Book;
import com.sudhanva.library_management_v2.Model.BorrowRecord;
import com.sudhanva.library_management_v2.Model.BorrowTransaction;
import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionItemResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionResponse;
import com.sudhanva.library_management_v2.repo.BookRepo;
import com.sudhanva.library_management_v2.repo.BorrowRecordRepo;
import com.sudhanva.library_management_v2.repo.BorrowTransactionRepo;
import com.sudhanva.library_management_v2.repo.MemberRepo;



@Service
public class BorrowTransactionService {

    final private BorrowTransactionRepo borrowTransactionRepo;
    final private MemberRepo memberRepo;
    final private BookRepo bookRepo;
    final private BorrowRecordRepo borrowRecordRepo;
    
    final private static int MAX_BOOKS = 5;

    BorrowTransactionService(
        BorrowTransactionRepo bTRepo,
        MemberRepo memberRepo,
        BookRepo bookRepo,
        BorrowRecordRepo borrowRecordRepo
    ) {
        this.borrowTransactionRepo = bTRepo;
        this.memberRepo = memberRepo;
        this.bookRepo = bookRepo;
        this.borrowRecordRepo = borrowRecordRepo;
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
            borrowTransactionRepo.findById(id).orElse(null);

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

        List<BorrowTransaction> borrowTransactionsList =  borrowTransactionRepo.findAll();

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

        List<BorrowTransaction> borrowTransactionsList =  borrowTransactionRepo.findByMemberId(memberId);

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


    // Borrow Book (Create Operation in Two Tables)
    @Transactional
    public ApiResponse<BorrowTransactionResponse> borrowBook(BorrowTransactionRequest borrowRequest) {

        Member existingMember = memberRepo.findById(borrowRequest.memberId()).orElse(null);

        // Validate Member

        if (existingMember == null) {
            return new ApiResponse<>(
                false,
                "No Member Exits" + borrowRequest.memberId(),
                null
            );
        }

        if (existingMember.getIsActive() == false){
            return new ApiResponse<>(
                false,
                "Member is inactive" +
                borrowRequest.memberId() + 
                " Name: "+existingMember.getName(),
                null
            );
        }


        // Max Book

        // Dont use this approch
        // Getting broowTransactions and .getBorrwRecord()
        // Performance low

        List<BorrowRecord> borrowRecords = borrowRecordRepo.findByBorrowTransactionMemberIdAndReturnDateIsNull(borrowRequest.memberId());

        // Here first part is redudent check
        if (
            borrowRecords.size() >= MAX_BOOKS || 
            ( borrowRecords.size() + borrowRequest.books().size()) > MAX_BOOKS
        ){
            return new ApiResponse<>(
                false,
                "Max Borrowing Books Exceeds",
                null
            );
        }


        // Validate Book

        // Duplicate Check
        List<Long> bookIds = borrowRequest.books()
            .stream()
            .map( book -> book.bookId())
            .toList();

        Set<Long> uniqueBookIds = new HashSet<>(bookIds);

        if (uniqueBookIds.size() != bookIds.size()) {
            return new ApiResponse<>(
                    false,
                    "Book Duplicate book IDs found in request.",
                    null
            );
        }


        List<Book> existingBooks = bookRepo.findAllById(bookIds);
        
        // Check if any book not present
        if(existingBooks.size() != bookIds.size()){

            Set<Long> foundIds = existingBooks.stream()
                .map(book -> book.getId())
                .collect(Collectors.toSet());
            
            Long missingBookId = bookIds.stream()
                .filter(id -> !foundIds.contains(id))
                .findFirst()
                .orElse(null);
            
            return new ApiResponse<>(
                false,
                "Book dosen't exist Id: " + missingBookId + "\n",
                null
            );
            
        }


        for (Book book : existingBooks) {

            // Check for inActive
            if (book.getIsActive() == false){
                return new ApiResponse<>(
                    false,
                    "Book is inactive" +
                    " Name: "+book.getName(),
                    null
                );
            }

            // Check for total copy
            if(book.getAvailableCopy() <= 0){
                return new ApiResponse<>(
                    false,
                    "Copy not available" +
                    " Name: "+book.getName(),
                    null
                );
            }

        }


        // Search for Return == null in borrow Reacords and get count ofit 


        // Borrow the Book
        LocalDateTime borrowDate = LocalDateTime.now();
        // For now later take borrow dates from users
        LocalDateTime dueDate = borrowDate.plusDays(15);


        // Create Borrow Records
        List<BorrowRecord> borrowRecord = new ArrayList<>();

        // Create Borrow Transaction
        BorrowTransaction transaction = BorrowTransaction.builder()
            .borrowDate(borrowDate)
            .member(existingMember)
            .records(borrowRecord)
            .build();
        

        // Create Book Records
        for (Book book : existingBooks) {
            borrowRecord.add(
                BorrowRecord.builder()
                    .borrowTransaction(transaction)
                    .book(book)
                    .returnDate(null)
                    // Later add a custom due date for all
                    .dueDate(dueDate)
                    .build()
            );
            // Decrese the available count
            book.setAvailableCopy(book.getAvailableCopy() - 1);
        }


        // Add to Database/ save transaction
        borrowTransactionRepo.save(transaction);


        // Make Repsonse
        return new ApiResponse<>(
            true,
            "Books borrowed successfully",
            mapToBorrowTransactionResponse(transaction)
        );

    }




}
