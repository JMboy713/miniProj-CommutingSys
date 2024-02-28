package com.group.commutesystem.service.member;

import com.group.commutesystem.dto.request.CreateMemberRequest;
import com.group.commutesystem.dto.response.MemberResponse;
import com.group.commutesystem.model.member.Member;
import com.group.commutesystem.model.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void createMember(CreateMemberRequest createMemberRequest) {
        Member member = new Member(createMemberRequest.getName(), createMemberRequest.getTeamName(), createMemberRequest.isRole(), createMemberRequest.getBirthday(), createMemberRequest.getWorkStartDate());
        memberRepository.save(member);
    }
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(member -> new MemberResponse(member.getName(), member.getTeamName(), member.isRole(), member.getBirthday(), member.getWorkStartDate())).collect(Collectors.toList());

    }
}
