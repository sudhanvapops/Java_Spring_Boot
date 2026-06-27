package com.sudhanva.library_management_v2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Member.MemberRequest;
import com.sudhanva.library_management_v2.Model.Dto.Member.MemberResponse;
import com.sudhanva.library_management_v2.Service.MemberService;

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
public class MemberController {

    private final MemberService memberService;

    MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // Get All Members
    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMember() {
        ApiResponse<List<MemberResponse>> member = memberService.getAllMembers();
        return ResponseEntity.status(HttpStatus.OK).body(member);
    }

    // Get Member with specific Id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(@PathVariable Long id) {
        ApiResponse<MemberResponse> response = memberService.getMemberById(id);
        if (response.success() == false) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Add a New Member
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> addMember(
            @Valid @RequestBody MemberRequest memberRequest) {

        ApiResponse<MemberResponse> response = memberService.addMember(memberRequest);

        if (!response.success()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Updtae a Member
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest memberRequest) {

        ApiResponse<MemberResponse> memberResponse = memberService.updateMember(id, memberRequest);

        if (memberResponse.success() == false) {
            if (memberResponse.message().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(memberResponse);
            }
            if (memberResponse.message().contains("inactive")) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(memberResponse);
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(memberResponse);
        }

        return ResponseEntity.status(HttpStatus.OK).body(memberResponse);
    }

    // Delete Member
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> deleteMember(
            @PathVariable Long id) {
        ApiResponse<MemberResponse> response = memberService.deleteMember(id);
        if (!response.success()) {

            if (response.message().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (response.message().contains("borrowed")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            if (response.message().contains("inactive")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Reactivate Member
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<MemberResponse>> activateMember(
        @PathVariable  Long id
    ){

        ApiResponse<MemberResponse> response = memberService.activateMember(id);

        if (!response.success()){
            if(response.message().contains("Not Found")){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}