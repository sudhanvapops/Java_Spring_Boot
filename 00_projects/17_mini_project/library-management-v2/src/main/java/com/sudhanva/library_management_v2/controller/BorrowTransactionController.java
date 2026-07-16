package com.sudhanva.library_management_v2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.BorrowRecord.BorrowTransactionResponse;
import com.sudhanva.library_management_v2.Service.BorrowTransactionService;



@RestController
@RequestMapping("/api/transaction")
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
            bTService.getBookById();

        // handle cases

        
        return null;
    }
    // Get All Transaction


    // Make a Transaction


    // No Delete

}
