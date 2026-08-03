package com.sudhanva.library_management_v2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Exception.ErrorResponseDto;
import com.sudhanva.library_management_v2.Model.Dto.Member.MemberRequest;
import com.sudhanva.library_management_v2.Model.Dto.Member.MemberResponse;
import com.sudhanva.library_management_v2.Service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RestController
@RequestMapping("/api/member")
@Tag(name = "Members", description = "Endpoints for managing library members")
public class MemberController {

    private final MemberService memberService;

    MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // Get All Members
    @Operation(summary = "Get all members")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Members found (empty list if none exist)",
        content = @Content(schema = @Schema(implementation = MemberResponse.class)))
    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMember() {
        ApiResponse<List<MemberResponse>> member = memberService.getAllMembers();
        return ResponseEntity.status(HttpStatus.OK).body(member);
    }

    // Get Member with specific Id
    @Operation(summary = "Get a member by id")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Member found",
        content = @Content(schema = @Schema(implementation = MemberResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No member exists with the given id",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(
            @Parameter(description = "Member id") @PathVariable Long id) {
        ApiResponse<MemberResponse> response = memberService.getMemberById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Add a New Member
    @Operation(summary = "Register a new member")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Member created",
        content = @Content(schema = @Schema(implementation = MemberResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request failed validation")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "A member with the same email already exists",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> addMember(
            @Valid @RequestBody MemberRequest memberRequest) {

        ApiResponse<MemberResponse> response = memberService.addMember(memberRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Updtae a Member
    @Operation(summary = "Update an existing member")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Member updated",
        content = @Content(schema = @Schema(implementation = MemberResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request failed validation")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No member exists with the given id",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Member is inactive, or another member already has the same email",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMember(
            @Parameter(description = "Member id") @PathVariable Long id,
            @Valid @RequestBody MemberRequest memberRequest) {

        ApiResponse<MemberResponse> memberResponse = memberService.updateMember(id, memberRequest);

        return ResponseEntity.status(HttpStatus.OK).body(memberResponse);
    }

    // Delete Member
    @Operation(summary = "Deactivate a member", description = "Soft-deletes a member by marking them inactive; they are not removed from the database")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Member deactivated",
        content = @Content(schema = @Schema(implementation = MemberResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No member exists with the given id",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Member is already inactive, or has active (unreturned) borrows",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> deleteMember(
            @Parameter(description = "Member id") @PathVariable Long id) {
        ApiResponse<MemberResponse> response = memberService.deleteMember(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Reactivate Member
    @Operation(summary = "Reactivate a previously deactivated member")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Member reactivated",
        content = @Content(schema = @Schema(implementation = MemberResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No member exists with the given id",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<MemberResponse>> activateMember(
        @Parameter(description = "Member id") @PathVariable  Long id
    ){

        ApiResponse<MemberResponse> response = memberService.activateMember(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
