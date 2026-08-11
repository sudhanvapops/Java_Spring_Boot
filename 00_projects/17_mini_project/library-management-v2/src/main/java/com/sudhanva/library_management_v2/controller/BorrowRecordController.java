package com.sudhanva.library_management_v2.controller;

import java.util.List;

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
import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.Service.BorrowRecordService;
import com.sudhanva.library_management_v2.security.authorization.StaffOnly;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/borrowrecord")
@Tag(name = "Borrow Records", description = "Endpoints for returning books and viewing individual borrow records")
@SecurityRequirement(name = "bearerAuth")
@StaffOnly
public class BorrowRecordController {

    final private BorrowRecordService borrowRecordService;

    public BorrowRecordController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    // Get All Records
    @Operation(summary = "Get all borrow records")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Records found", content = @Content(schema = @Schema(implementation = BorrowTransactionItemResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No borrow records exist", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BorrowTransactionItemResponse>>> getAllRecords() {

        ApiResponse<List<BorrowTransactionItemResponse>> response = borrowRecordService.getAllRecords();

        return ResponseEntity.ok(response);
    }

    // All Record Of Member
    @Operation(summary = "Get all borrow records for a member")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Records found", content = @Content(schema = @Schema(implementation = BorrowTransactionItemResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No member exists with the given id, or the member has no borrow records", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/member-id/{memberId}")
    public ResponseEntity<ApiResponse<List<BorrowTransactionItemResponse>>> getRecordsOfMemberId(
            @Parameter(description = "Member id") @PathVariable Long memberId) {

        ApiResponse<List<BorrowTransactionItemResponse>> response = borrowRecordService.getAllMemberRecords(memberId);

        return ResponseEntity.ok(response);
    }

    // Get all unreturned books
    @Operation(summary = "Get all unreturned (currently borrowed) books")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Unreturned records found", content = @Content(schema = @Schema(implementation = BorrowTransactionItemResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No unreturned books found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/unreturned/all")
    public ResponseEntity<ApiResponse<List<BorrowTransactionItemResponse>>> getAllUnreturendBooks() {

        ApiResponse<List<BorrowTransactionItemResponse>> response = borrowRecordService.getAllUnreturnedRecords();

        return ResponseEntity.ok(response);
    }

    // unreturned
    @Operation(summary = "Get all unreturned (currently borrowed) books for a member")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Unreturned records found", content = @Content(schema = @Schema(implementation = BorrowTransactionItemResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No member exists with the given id, or the member has no unreturned books", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/unreturned/{memberId}")
    public ResponseEntity<ApiResponse<List<BorrowTransactionItemResponse>>> getAllUnreturendBooksOfMember(
            @Parameter(description = "Member id") @PathVariable Long memberId) {

        ApiResponse<List<BorrowTransactionItemResponse>> response = borrowRecordService
                .getAllUnreturnedRecordsOfMember(memberId);

        return ResponseEntity.ok(response);

    }

    // Today Due Books
    @Operation(summary = "Get all records due today")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Due records found (empty list if none are due today)", content = @Content(schema = @Schema(implementation = DueTodayResponse.class)))
    @GetMapping("/due-today")
    public ResponseEntity<ApiResponse<List<DueTodayResponse>>> getDueRecordsToday() {
        ApiResponse<List<DueTodayResponse>> response = borrowRecordService.getDueRecordsToday();
        return ResponseEntity.ok(response);
    }

    // Return Book
    // Post request Becaause
    // because invoking an action: "return these books."
    // You're not directly updating a single resource representation. You're
    // performing a business operation that:
    // Updates BorrowRecord
    // Updates Book.availableCopy
    // Calculates a fine
    // Returns a summary response

    @Operation(summary = "Return one or more borrowed books", description = "Marks the given books as returned, calculates any overdue fine, "
            + "and increases their available copy count. returnDate defaults to now if not provided.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Books returned successfully", content = @Content(schema = @Schema(implementation = BookReturnResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request failed validation, duplicate books in request, "
            + "member has no active borrowed books, one or more books were not borrowed by this member, return date is in the future, "
            + "or the underlying fine-per-day setting value is malformed")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No member exists with the given id, "
            + "or the fine-per-day library setting has not been configured", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Member is inactive", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PostMapping("/return")
    public ResponseEntity<ApiResponse<BookReturnResponse>> returnBooks(
            @Valid @RequestBody BookReturnRequest bookReturnRequest) {

        ApiResponse<BookReturnResponse> response = borrowRecordService.returnBook(bookReturnRequest);

        return ResponseEntity.ok(response);
    }

}
