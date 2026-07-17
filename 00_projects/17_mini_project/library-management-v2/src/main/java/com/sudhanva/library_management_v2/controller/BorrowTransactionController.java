package com.sudhanva.library_management_v2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionResponse;
import com.sudhanva.library_management_v2.Service.BorrowTransactionService;



@RestController
@RequestMapping("/api/borrow-transactions")
public class BorrowTransactionController {
    
    
    final private BorrowTransactionService bTService;

    public BorrowTransactionController(BorrowTransactionService bTService) {
        this.bTService = bTService;
    }


    // Get A Transaction
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowTransactionResponse>> getTransaction (
        @PathVariable Long id
    ) {

        ApiResponse<BorrowTransactionResponse> response = 
            bTService.getTransactionById(id);

        // handle cases
        if (response.success() == false){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        return ResponseEntity.ok(response);
    }


    // Get All Transaction

    @GetMapping("/all")
    ResponseEntity<ApiResponse<List<BorrowTransactionResponse>>> getAllTransactions(){

        ApiResponse<List<BorrowTransactionResponse>> response = bTService.getAllTransaction();

        if(response.success()==false){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }

    // Get ALl Transaction belong to a Single Person
    @GetMapping("/member-id/{memberId}")
    ResponseEntity<ApiResponse<List<BorrowTransactionResponse>>> getAllTransactionsByMemberId(@PathVariable Long memberId){


        ApiResponse<List<BorrowTransactionResponse>> response = bTService.getAllTransactionByMemeberId(memberId);

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

    // Make a Transaction


    // No Delete

}
