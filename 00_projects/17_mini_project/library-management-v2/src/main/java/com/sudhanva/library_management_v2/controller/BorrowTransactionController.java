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
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionRequest;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionResponse;
import com.sudhanva.library_management_v2.Service.BorrowTransactionService;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/borrow-transactions")
public class BorrowTransactionController {
    
    
    final private BorrowTransactionService borrowTransactionService;

    public BorrowTransactionController(BorrowTransactionService bTService) {
        this.borrowTransactionService = bTService;
    }


    // Get A Transaction
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowTransactionResponse>> getTransaction (
        @PathVariable Long id
    ) {

        ApiResponse<BorrowTransactionResponse> response = 
            borrowTransactionService.getTransactionById(id);

        // handle cases
        if (response.success() == false){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        return ResponseEntity.ok(response);
    }


    // Get All Transaction

    @GetMapping("/all")
    ResponseEntity<ApiResponse<List<BorrowTransactionResponse>>> getAllTransactions(){

        ApiResponse<List<BorrowTransactionResponse>> response = borrowTransactionService.getAllTransaction();

        if(response.success()==false){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }


    // Get ALl Transaction belong to a Single Person
    @GetMapping("/member-id/{memberId}")
    ResponseEntity<ApiResponse<List<BorrowTransactionResponse>>> getAllTransactionsByMemberId(@PathVariable Long memberId){


        ApiResponse<List<BorrowTransactionResponse>> response = borrowTransactionService.getAllTransactionByMemeberId(memberId);

        if(response.success()==false){
            if(response.message().strip().contains("Member")){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            if(response.message().strip().contains("Transaction")){
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }

        return ResponseEntity.ok(response);
    }


   @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<BorrowTransactionResponse>> borrowBook(
        @Valid @RequestBody BorrowTransactionRequest borrowRequest
    ) {
    
        ApiResponse<BorrowTransactionResponse> response =
                borrowTransactionService.borrowBook(borrowRequest);
    
        if (!response.success()) {
    
            String message = response.message().toLowerCase();
    
            if (message.contains("member exits") || message.contains("book dosen't exist")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
    
            if (message.contains("inactive")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
    
            if (message.contains("duplicate")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
    
            if (message.contains("copy not available")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
    
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    
    }

    // No Delete

}
