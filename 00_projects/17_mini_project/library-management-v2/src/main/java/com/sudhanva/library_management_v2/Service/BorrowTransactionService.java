package com.sudhanva.library_management_v2.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
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
import com.sudhanva.library_management_v2.enums.Setting.SettingKey;
import com.sudhanva.library_management_v2.repo.BookRepo;
import com.sudhanva.library_management_v2.repo.BorrowRecordRepo;
import com.sudhanva.library_management_v2.repo.BorrowTransactionRepo;
import com.sudhanva.library_management_v2.repo.MemberRepo;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberNotFoundException;
import com.sudhanva.library_management_v2.exceptions.MemberExceptions.MemberInactiveException;
import com.sudhanva.library_management_v2.exceptions.BorrowTransactionExceptions.BorrowTransactionNotFoundException;
import com.sudhanva.library_management_v2.exceptions.BorrowTransactionExceptions.NoBorrowTransactionsFoundException;
import com.sudhanva.library_management_v2.exceptions.BorrowTransactionExceptions.MaxBookLimitExceededException;
import com.sudhanva.library_management_v2.exceptions.BorrowTransactionExceptions.BookAlreadyBorrowedByMemberException;
import com.sudhanva.library_management_v2.exceptions.BorrowExceptions.DuplicateBookRequestException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookNotFoundException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookInactiveException;
import com.sudhanva.library_management_v2.exceptions.BookExceptions.BookNotAvailableException;



@Service
public class BorrowTransactionService {

    final private BorrowTransactionRepo borrowTransactionRepo;
    final private MemberRepo memberRepo;
    final private BookRepo bookRepo;
    final private BorrowRecordRepo borrowRecordRepo;
    final private LibrarySettingsService librarySettingsService;

    BorrowTransactionService(
        BorrowTransactionRepo bTRepo,
        MemberRepo memberRepo,
        BookRepo bookRepo,
        BorrowRecordRepo borrowRecordRepo,
        LibrarySettingsService librarySettingsService
    ) {
        this.borrowTransactionRepo = bTRepo;
        this.memberRepo = memberRepo;
        this.bookRepo = bookRepo;
        this.borrowRecordRepo = borrowRecordRepo;
        this.librarySettingsService = librarySettingsService;
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
            borrowTransactionRepo.findById(id)
                .orElseThrow(() -> new BorrowTransactionNotFoundException(id));

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
            throw new NoBorrowTransactionsFoundException();
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

        Member existingMember = memberRepo.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(memberId));

        List<BorrowTransaction> borrowTransactionsList =  borrowTransactionRepo.findByMemberId(memberId);

        if(borrowTransactionsList.isEmpty()){
            throw new NoBorrowTransactionsFoundException(memberId);
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

        Member existingMember = memberRepo.findById(borrowRequest.memberId())
            .orElseThrow(() -> new MemberNotFoundException(borrowRequest.memberId()));

        // Validate Member

        if (existingMember.getIsActive() == false){
            throw new MemberInactiveException(borrowRequest.memberId());
        }


        // Dont use this approch
        // Getting broowTransactions and .getBorrwRecord()
        // Performance low

        List<BorrowRecord> borrowExisitingRecords = borrowRecordRepo.findByBorrowTransactionMemberIdAndReturnDateIsNull(borrowRequest.memberId());

        // Max Book
        int maxBooks = librarySettingsService.getIntSetting(SettingKey.MAX_BOOKS);

        // Here first part is redudent check
        if (
            borrowExisitingRecords.size() >= maxBooks ||
            ( borrowExisitingRecords.size() + borrowRequest.books().size()) > maxBooks
        ){
            throw new MaxBookLimitExceededException(maxBooks);
        }



        // Validate Book

        // Duplicate Check
        List<Long> bookIds = borrowRequest.books()
            .stream()
            .map( book -> book.bookId())
            .toList();

        Set<Long> uniqueBookIds = new HashSet<>(bookIds);

        if (uniqueBookIds.size() != bookIds.size()) {
            throw new DuplicateBookRequestException();
        }



        // Check if borrower already have that book
        Set<Long>borrowedBookIds = borrowExisitingRecords.stream().map( item -> item.getBook().getId() ).collect(Collectors.toSet());

        List<Long> duplicateBookIds = bookIds.stream()
            .filter(borrowedBookIds::contains)
            .toList();

        if (!duplicateBookIds.isEmpty()) {
            throw new BookAlreadyBorrowedByMemberException(duplicateBookIds);
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

            throw BookNotFoundException.byId(missingBookId);

        }


        for (Book book : existingBooks) {

            // Check for inActive
            if (book.getIsActive() == false){
                throw new BookInactiveException(book.getId());
            }

            // Check for total copy
            if(book.getAvailableCopy() <= 0){
                throw new BookNotAvailableException(book.getId());
            }

        }


        // Borrow the Book
        LocalDateTime borrowDate = LocalDateTime.now();
        // For now later take borrow dates from users
        int maxBorrowDays = librarySettingsService.getIntSetting(SettingKey.MAX_BORROW_DAYS);
        LocalDateTime dueDate = borrowDate.plusDays(maxBorrowDays);


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
