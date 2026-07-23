package com.sudhanva.library_management_v2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BookReturnRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BookReturnResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionItemResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.DueTodayResponse;
import com.sudhanva.library_management_v2.Service.BorrowRecordService;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/borrowrecord")
public class BorrowRecordController {
    
    final private BorrowRecordService borrowRecordService;

    public BorrowRecordController(BorrowRecordService borrowRecordService){
        this.borrowRecordService = borrowRecordService;
    }


    // Get All Records
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BorrowTransactionItemResponse>>> getAllRecords(){

        ApiResponse<List<BorrowTransactionItemResponse>> response = borrowRecordService.getAllRecords();

        if (response.success() == false){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.ok(response);
    }


    // All Record Of Member
    @GetMapping("/member-id/{memberId}")
    public ResponseEntity<ApiResponse<List<BorrowTransactionItemResponse>>> getRecordsOfMemberId(
        @PathVariable Long memberId
    ){

        ApiResponse<List<BorrowTransactionItemResponse>> response = borrowRecordService.getAllMemberRecords(memberId);

        if (response.success() == false){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(response);
    }


    // unreturned
    @GetMapping("/unreturned/{memberId}")
    public ResponseEntity<ApiResponse<List<BorrowTransactionItemResponse>>> getAllUnreturendBooks(@PathVariable Long memberId){

        ApiResponse<List<BorrowTransactionItemResponse>> response = borrowRecordService.getAllUnreturnedRecords(memberId);

        if (response.success() == false){
            if(response.message().strip().toLowerCase().contains("member")){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            if(response.message().strip().toLowerCase().contains("borrow")){
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        
        return ResponseEntity.ok(response);

    }


    // Today Due Books
    @GetMapping("/due-today")
    public ResponseEntity<ApiResponse<List<DueTodayResponse>>> getDueRecordsToday() {
        ApiResponse<List<DueTodayResponse>> response =
                borrowRecordService.getDueRecordsToday();
        return ResponseEntity.ok(response);
    }


    // Return Book
    // Post request Becaause
    // because invoking an action: "return these books."
    // You're not directly updating a single resource representation. You're performing a business operation that:
    // Updates BorrowRecord
    // Updates Book.availableCopy
    // Calculates a fine
    // Returns a summary response

    @PostMapping("/return")
    public ResponseEntity<ApiResponse<BookReturnResponse>> returnBooks(
        @Valid @RequestBody BookReturnRequest bookReturnRequest
    ) {

        ApiResponse<BookReturnResponse> response =
                borrowRecordService.returnBook(bookReturnRequest);

        if (!response.success()) {

            String message = response.message().toLowerCase();

            if (message.contains("member not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (message.contains("inactive")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if (message.contains("duplicate")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (message.contains("not currently borrowed")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (message.contains("no active borrowed books")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.ok(response);
    }

}
