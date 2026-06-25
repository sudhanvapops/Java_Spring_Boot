package com.sudhanva.library_management_v2.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Model.Dto.ApiResponse.ApiResponse;
import com.sudhanva.library_management_v2.Model.Dto.Member.MemberRequest;
import com.sudhanva.library_management_v2.Model.Dto.Member.MemberResponse;
import com.sudhanva.library_management_v2.repo.MemberRepo;

@Service
public class MemberService {

    @Autowired
    private MemberRepo memberRepo;


    // Utility Methods
    private MemberResponse mapToMemberResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .age(member.getAge())
                .build();
    }

    private Member mapToMember(MemberRequest memberRequest) {
        return Member.builder()
                .name(memberRequest.name())
                .email(memberRequest.email())
                .age(memberRequest.age())
                .build();
    }

    // Service Methods

    public ApiResponse<List<MemberResponse>> getAllMembers() {

        List<Member> members = memberRepo.findAll();
        List<MemberResponse> allMembers = new ArrayList<>();

        for (Member member : members) {
            allMembers.add(mapToMemberResponse(member));
        }

        return new ApiResponse<>(
            true,
            "Total Members founs: "+allMembers.size(),
            allMembers
        );
    }


    public ApiResponse<MemberResponse> getMemberById(Long id) {

        Member member = memberRepo.findById(id)
                .orElse(null);

        if (member == null) {
            return new ApiResponse<>(
            false,
            "Member Not Found with id: "+id,
            null
        );
        }

        return new ApiResponse<>(
            true,
            "Member Fetched Successfully",
            mapToMemberResponse(member)
        );
    }

    // Add Member
    public ApiResponse<MemberResponse> addMember(MemberRequest memberRequest) {

        Member existingMember = memberRepo.findByEmail(memberRequest.email()).orElse(null);

        if (existingMember != null) {
            return new ApiResponse<>(
                false,
                "Email already exists: " + memberRequest.email(),
                null
            );
        }

        Member member = mapToMember(memberRequest);

        return new ApiResponse<>(
            true,
            "Member Created successfully",
            mapToMemberResponse(memberRepo.save(member))
        );
    }

    public ApiResponse<MemberResponse> updateMember(Long id, MemberRequest memberRequest) {

        Member existingMember = memberRepo.findById(id).orElse(null);

        if (existingMember == null) {
            return new ApiResponse<>(false, "Member not found with id: " + id, null);
        }

        Optional<Member> memberWithEmail = memberRepo.findByEmail(memberRequest.email());

        if (memberWithEmail.isPresent() && !memberWithEmail.get().getId().equals(id)) {
            return new ApiResponse<>(false, "Email already exists for another member", null);
        }

        existingMember.setAge(memberRequest.age());
        existingMember.setEmail(memberRequest.email());
        existingMember.setName(memberRequest.name());

        return new ApiResponse<>(
            true,
            "Member updated successfully",
            mapToMemberResponse(memberRepo.save(existingMember))
        );
    }


    // Delete Member
    public ApiResponse<MemberResponse> deleteMember(Long id) {

        Member member = memberRepo.findById(id).orElse(null);

        if(member == null){
            return new ApiResponse<>(
                false,
                "Member Not Found: "+ id,
                null
            );
        }

        memberRepo.delete(member);

        return new ApiResponse<>(
            true,
            "Member Deleted: "+id,
            mapToMemberResponse(member)
        );
    }

}
