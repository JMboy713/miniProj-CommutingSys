package com.group.commutesystem.controller.member;

import com.group.commutesystem.dto.request.CreateMemberRequest;
import com.group.commutesystem.service.member.MemberService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
