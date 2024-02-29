package com.group.commutesystem.controller.member;

import com.group.commutesystem.dto.member.request.CreateMemberRequest;
import com.group.commutesystem.dto.member.request.GoToWorkRequest;
import com.group.commutesystem.dto.member.response.MemberResponse;
import com.group.commutesystem.service.member.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/member")
    public void createMember(@RequestBody CreateMemberRequest request){
        memberService.createMember(request);
    }

    @GetMapping("/member")
    public List<MemberResponse> getMembers(){
        return memberService.getMembers();
    }

    @PostMapping("/member/gotowork")
    public void goToWork(@RequestBody GoToWorkRequest request){
        memberService.goToWork(request.getId());
    }
}
