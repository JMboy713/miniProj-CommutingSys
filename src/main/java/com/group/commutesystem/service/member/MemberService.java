package com.group.commutesystem.service.member;

import com.group.commutesystem.dto.member.request.CreateMemberRequest;
import com.group.commutesystem.dto.member.response.MemberResponse;
import com.group.commutesystem.model.member.Member;
import com.group.commutesystem.model.member.MemberRepository;
import com.group.commutesystem.model.team.Team;
import com.group.commutesystem.model.team.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    public MemberService(MemberRepository memberRepository, TeamRepository teamRepository) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public void createMember(CreateMemberRequest createMemberRequest) {
        Team team = teamRepository.findByName(createMemberRequest.getTeamName())
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));
        Member member = new Member(createMemberRequest, team);
        memberRepository.save(member);
    }
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(member -> new MemberResponse(member.getName(), member.getTeam().getName(), member.isRole(), member.getBirthday(), member.getWorkStartDate())).collect(Collectors.toList());

    }
}
