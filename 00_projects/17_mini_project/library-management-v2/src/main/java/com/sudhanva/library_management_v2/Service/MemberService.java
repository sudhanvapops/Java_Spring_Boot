package com.sudhanva.library_management_v2.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sudhanva.library_management_v2.Model.Member;
import com.sudhanva.library_management_v2.Model.Dto.Member.MemberRequest;
import com.sudhanva.library_management_v2.Model.Dto.Member.MemberResponse;
import com.sudhanva.library_management_v2.repo.MemberRepo;

@Service
public class MemberService {

    @Autowired
    private MemberRepo memberRepo;

    private MemberResponse mapToMemberResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .age(member.getAge())
                .build();
    }

    public List<MemberResponse> getAllMembers() {

        List<Member> members = memberRepo.findAll();
        List<MemberResponse> allMembers = new ArrayList<>();

        for (Member member : members) {
            allMembers.add(mapToMemberResponse(member));
        }
        return allMembers;
    }

    public MemberResponse getMemberById(Long id) {

        Member member = memberRepo.findById(id)
                .orElse(null);

        if (member == null) {
            return null;
        }

        return mapToMemberResponse(member);

    }

    public MemberResponse addMember(MemberRequest memberRequest) {

        Member existingMember = memberRepo.findByEmail(memberRequest.email()).orElse(null);

        if (existingMember != null) {
            return null;
        }

        Member member = Member.builder()
                .name(memberRequest.name())
                .email(memberRequest.email())
                .age(memberRequest.age())
                .build();

        return mapToMemberResponse(memberRepo.save(member));
    }

}
