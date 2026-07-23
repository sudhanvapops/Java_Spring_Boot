package com.sudhanva.library_management_v2.Service;

import com.sudhanva.library_management_v2.repo.BookRepo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sudhanva.library_management_v2.Model.Book;
import com.sudhanva.library_management_v2.Model.BorrowRecord;
import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BookReturnRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BookReturnResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowReturnItemRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionItemResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.DueTodayResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowReturnItemResponse;
import com.sudhanva.library_management_v2.repo.BorrowRecordRepo;
import com.sudhanva.library_management_v2.repo.MemberRepo;




@Service
public class BorrowRecordService {

    private final BookRepo bookRepo;
    final private BorrowRecordRepo borrowRecordRepo;
    final private MemberRepo memberRepo;

    final private int FINE = 10;

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

    // borrowRecord.getBorrowTransaction().getMember().getName();
    // If i writ ethe above hibernate may execute many quires caus eof lazy loading
    // and maytoone relation is oftern Lazy
   @Transactional(readOnly = true)
    public ApiResponse<List<DueTodayResponse>> getDueRecordsToday() {

        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        List<DueTodayResponse> records =
                borrowRecordRepo.findDueToday(start, end);

        return new ApiResponse<>(
                true,
                "Due Records: " + records.size(),
                records
        );
    }

    // Return Book
    @Transactional
    public ApiResponse<BookReturnResponse> returnBook(
        BookReturnRequest bookReturnRequest
    ){

        // Validate Member
        Member existingMember = memberRepo.findById(bookReturnRequest.memberId()).orElse(null);

        if(existingMember == null){
            return new ApiResponse<>(
                false,
                "Member not found",
                null
            );
        }


        if(!existingMember.getIsActive()){
            return new ApiResponse<>(
                false,
                "Member is inactive",
                null
            );
        }


        // Validate Books


        // Requested Book Ids
        List<Long> returningBookIds = bookReturnRequest.books()
            .stream()
            .map( book -> book.bookId())
            .toList();


        Set<Long> uniqueBookIds = new HashSet<>(returningBookIds);
        if (uniqueBookIds.size() != returningBookIds.size()) {
            return new ApiResponse<>(
                false,
                "Book Duplicate book IDs found in request.",
                null
            );
        }


        // Get all active borrowed books of the member
        List<BorrowRecord> activeBorrowedBooks = borrowRecordRepo.findByBorrowTransactionMemberIdAndReturnDateIsNull(bookReturnRequest.memberId());


        if (activeBorrowedBooks.isEmpty()){
            return new ApiResponse<>(
                false,
                "Member has no active borrowed books.",
                null
            );
        }


        // Map Active Nooks: bookId -> BorrowRecord
        Map<Long,BorrowRecord> borrowedBookMap = activeBorrowedBooks.stream().collect(
            Collectors.toMap(
                record -> record.getBook().getId(),
                record -> record
            )
        );
        

        // Validate Requested Book are actually borrowed By The borrower or other

        List<Long> notBelongsBookIds = new ArrayList<>();

        for (Long bookId : returningBookIds) {
            if (!borrowedBookMap.containsKey(bookId)) {
                notBelongsBookIds.add(bookId);
            }
        }
        
        if (!notBelongsBookIds.isEmpty()) {
            return new ApiResponse<>(
                    false,
                    "Book Id " + notBelongsBookIds + " is not currently borrowed by this member.",
                    null
            );
        }


        // Return Each Book

        List<BorrowReturnItemResponse> returnedBooks = new ArrayList<>();

        BigDecimal totalFine = BigDecimal.ZERO;
        LocalDateTime returnDate = LocalDateTime.now();
        
        for (Long bookId : returningBookIds) {

            BorrowRecord record = borrowedBookMap.get(bookId);
            
            // Set Return Date
            record.setReturnDate(returnDate);


            // Set Fine
            long daysLate = Math.max(
                0,
                ChronoUnit.DAYS.between(
                    record.getDueDate().toLocalDate(),
                    returnDate.toLocalDate()
                )
            );

            BigDecimal fine = BigDecimal.valueOf(daysLate * FINE);

            record.setFine(fine);

            totalFine = totalFine.add(fine);

            // Increase available copies
            Book book = record.getBook();
            book.setAvailableCopy(book.getAvailableCopy() + 1);

            // Response Item
            returnedBooks.add(
                BorrowReturnItemResponse.builder()
                    .bookId(book.getId())
                    .bookName(book.getName())
                    .fine(fine)
                    .returnDate(returnDate)
                    .build()
            );

        }

        BookReturnResponse response =
            BookReturnResponse.builder()
                    .memberId(existingMember.getId())
                    .memberName(existingMember.getName())
                    .books(returnedBooks)
                    .totalFine(totalFine)
                    .build();

        return new ApiResponse<>(
            true,
            "Book returned successfully",
            response
        );
    }

}