package com.sudhanva.library_management_v2.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Member.MemberRequest;
import com.sudhanva.library_management_v2.Model.Dto.Member.MemberResponse;
import com.sudhanva.library_management_v2.exceptions.MemberNotFoundException;
import com.sudhanva.library_management_v2.repo.MemberRepo;

@Service
public class MemberService {

    private final MemberRepo memberRepo;

    public MemberService(MemberRepo memberRepo) {
        this.memberRepo = memberRepo;
    }

    // Utility Methods
    private MemberResponse mapToMemberResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .age(member.getAge())
                .isActive(member.getIsActive())
                .build();
    }

    private Member mapToMember(MemberRequest memberRequest) {
        return Member.builder()
                .name(memberRequest.name())
                .email(memberRequest.email())
                .age(memberRequest.age())
                .isActive(memberRequest.isActive())
                .build();
    }

    // Service Methods

    @Transactional(readOnly = true)
    public ApiResponse<List<MemberResponse>> getAllMembers() {

        List<Member> members = memberRepo.findAll();
        List<MemberResponse> allMembers = new ArrayList<>();

        for (Member member : members) {
            allMembers.add(mapToMemberResponse(member));
        }

        return new ApiResponse<>(
                true,
                "Total Members found: " + allMembers.size(),
                allMembers);
    }
    

    @Transactional(readOnly = true)
    public ApiResponse<MemberResponse> getMemberById(Long id) {

        Member existingMember = 
                memberRepo.findById(id)
                    .orElseThrow(() -> new MemberNotFoundException(id));

        return new ApiResponse<>(
                true,
                "Member Fetched Successfully",
                mapToMemberResponse(existingMember));
    }


    // Add Member

    @Transactional
    public ApiResponse<MemberResponse> addMember(MemberRequest memberRequest) {

        Member existingMember = memberRepo.findByEmail(memberRequest.email()).orElse(null);

        if (existingMember != null) {
            return new ApiResponse<>(
                    false,
                    "Email already exists: " + memberRequest.email(),
                    null);
        }

        Member member = mapToMember(memberRequest);

        return new ApiResponse<>(
                true,
                "Member Created successfully",
                mapToMemberResponse(memberRepo.save(member)));
    }

    // Update Member
    @Transactional
    public ApiResponse<MemberResponse> updateMember(Long id, MemberRequest memberRequest) {

        Member existingMember = memberRepo.findById(id).orElse(null);

        if (existingMember == null) {
            return new ApiResponse<>(false, "Member not found with id: " + id, null);
        }

        Optional<Member> memberWithEmail = memberRepo.findByEmail(memberRequest.email());

        if (memberWithEmail.isPresent() && !memberWithEmail.get().getId().equals(id)) {
            return new ApiResponse<>(false, "Email already exists for another member", null);
        }

        if (Boolean.FALSE.equals(existingMember.getIsActive())) {
            return new ApiResponse<>(false, "Member is inactive cannot update", null);
        }

        existingMember.setAge(memberRequest.age());
        existingMember.setEmail(memberRequest.email());
        existingMember.setName(memberRequest.name());

        return new ApiResponse<>(
                true,
                "Member updated successfully",
                mapToMemberResponse(memberRepo.save(existingMember)));
    }

    // Delete Member
    @Transactional
    public ApiResponse<MemberResponse> deleteMember(Long id) {

        Member member = memberRepo.findById(id).orElse(null);

        if (member == null) {
            return new ApiResponse<>(
                    false,
                    "Member Not Found: " + id,
                    null);
        }

        if (!member.getIsActive()) {
            return new ApiResponse<>(
                    false,
                    "Member is already inactive.",
                    null);
        }

        boolean hasActiveBorrow = member.getBorrowTransactions()
                .stream()
                .flatMap(transaction -> transaction.getRecords().stream())
                .anyMatch(record -> record.getReturnDate() == null);

        if (hasActiveBorrow) {
            return new ApiResponse<>(
                    false,
                    "Member has borrowed books that are not yet returned.",
                    null);
        }

        member.setIsActive(false);

        return new ApiResponse<>(
                true,
                "Member deactivated: " + id,
                mapToMemberResponse(member));
    }

    // Reactive Member
    public ApiResponse<MemberResponse> activateMember(Long id) {

        Member existingMember = memberRepo.findById(id).orElse(null);

        if (existingMember == null) {
            return new ApiResponse<>(
                    false,
                    "Member Not Found: " + id,
                    null);
        }

        // If already active sedning another active request doesnt matter so skip
        // validation for that

        existingMember.setIsActive(true);

        Member member = memberRepo.save(existingMember);

        return new ApiResponse<>(
                true,
                "Member Reactivated: " + id,
                mapToMemberResponse(member));

    }

}
