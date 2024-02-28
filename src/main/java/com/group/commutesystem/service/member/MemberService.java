package com.group.commutesystem.service.member;

import com.group.commutesystem.dto.request.CreateMemberRequest;
import com.group.commutesystem.model.member.Member;
import com.group.commutesystem.model.member.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void createMember(CreateMemberRequest createMemberRequest) {
        Member member = new Member(createMemberRequest.getName(), createMemberRequest.getTeamName(), createMemberRequest.isRole(), createMemberRequest.getBirthday(), createMemberRequest.getWorkStartDate());
        memberRepository.save(member);
    }
}
