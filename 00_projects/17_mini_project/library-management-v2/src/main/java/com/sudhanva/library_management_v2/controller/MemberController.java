package com.sudhanva.library_management_v2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Model.Dto.Member.MemberRequest;
import com.sudhanva.library_management_v2.Model.Dto.Member.MemberResponse;
import com.sudhanva.library_management_v2.Service.MemberService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getMember() {
        List<MemberResponse> member = memberService.getAllMembers();
        return ResponseEntity.status(HttpStatus.OK).body(member);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        MemberResponse member = memberService.getMemberById(id);
        if (member != null) {
            return ResponseEntity.status(HttpStatus.OK).body(member);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping
    public ResponseEntity<MemberResponse> addMember(
            @Valid @RequestBody MemberRequest memberRequest) {

        MemberResponse memberResponse = memberService.addMember(memberRequest);

        if (memberResponse != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();

    }
}