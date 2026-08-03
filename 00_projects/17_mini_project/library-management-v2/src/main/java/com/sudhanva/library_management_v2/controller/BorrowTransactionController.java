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
import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.Service.BorrowTransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/borrow-transactions")
@Tag(name = "Borrow Transactions", description = "Endpoints for borrowing books and viewing borrow transactions")
public class BorrowTransactionController {


    final private BorrowTransactionService borrowTransactionService;

    public BorrowTransactionController(BorrowTransactionService bTService) {
        this.borrowTransactionService = bTService;
    }


    // Get A Transaction
    @Operation(summary = "Get a borrow transaction by id")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transaction found",
        content = @Content(schema = @Schema(implementation = BorrowTransactionResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No transaction exists with the given id",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowTransactionResponse>> getTransaction (
        @Parameter(description = "Borrow transaction id") @PathVariable Long id
    ) {

        ApiResponse<BorrowTransactionResponse> response =
            borrowTransactionService.getTransactionById(id);

        return ResponseEntity.ok(response);
    }


    // Get All Transaction

    @Operation(summary = "Get all borrow transactions")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transactions found",
        content = @Content(schema = @Schema(implementation = BorrowTransactionResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No borrow transactions exist",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/all")
    ResponseEntity<ApiResponse<List<BorrowTransactionResponse>>> getAllTransactions(){

        ApiResponse<List<BorrowTransactionResponse>> response = borrowTransactionService.getAllTransaction();

        return ResponseEntity.ok(response);
    }


    // Get ALl Transaction belong to a Single Person
    @Operation(summary = "Get all borrow transactions for a member")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transactions found",
        content = @Content(schema = @Schema(implementation = BorrowTransactionResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No member exists with the given id, or the member has no borrow transactions",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/member-id/{memberId}")
    ResponseEntity<ApiResponse<List<BorrowTransactionResponse>>> getAllTransactionsByMemberId(
        @Parameter(description = "Member id") @PathVariable Long memberId
    ){


        ApiResponse<List<BorrowTransactionResponse>> response = borrowTransactionService.getAllTransactionByMemeberId(memberId);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Borrow one or more books for a member")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Books borrowed successfully",
        content = @Content(schema = @Schema(implementation = BorrowTransactionResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request failed validation, duplicate books in request, "
        + "member would exceed the max-books-per-member setting, or the underlying library setting value is malformed")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No member/book exists with the given id, "
        + "or a required library setting (e.g. max books, max borrow days) has not been configured",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Member is inactive, book is inactive, "
        + "book has no available copies, or the member already has this book borrowed",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
   @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<BorrowTransactionResponse>> borrowBook(
        @Valid @RequestBody BorrowTransactionRequest borrowRequest
    ) {

        ApiResponse<BorrowTransactionResponse> response =
                borrowTransactionService.borrowBook(borrowRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    // No Delete

}
