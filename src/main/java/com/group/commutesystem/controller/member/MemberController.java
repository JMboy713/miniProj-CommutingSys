package com.group.commutesystem.controller.member;

import com.group.commutesystem.dto.member.request.CreateMemberRequest;
import com.group.commutesystem.dto.member.request.CreateVacationRequest;
import com.group.commutesystem.dto.member.request.WorkRequest;
import com.group.commutesystem.dto.member.response.MemberResponse;
import com.group.commutesystem.dto.member.response.WorkResponse;
import com.group.commutesystem.service.member.MemberService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;

@RestController
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/member")
    public void createMember(@RequestBody CreateMemberRequest request) {
        memberService.createMember(request);
    }

    @GetMapping("/member")
    public List<MemberResponse> getMembers() {
        return memberService.getMembers();
    }

    @PostMapping("/member/gotowork")
    public void goToWork(@RequestBody WorkRequest request) {
        memberService.goToWork(request.getId());
    }

    @PutMapping("/member/getoffwork")
    public void getOffWork(@RequestBody WorkRequest request) {
        memberService.getOffWork(request.getId());
    }

    @GetMapping("/member/commute")
    public WorkResponse getCommuteHistory(@RequestParam Long memberId, @RequestParam String date) {
        YearMonth yearMonth = YearMonth.parse(date);
        return memberService.getCommuteHistory(memberId, yearMonth);
    }

    @PostMapping("member/vacation")
    public void requestVacation(@RequestBody CreateVacationRequest request) {
        LocalDate date = LocalDate.parse(request.getDate());
        memberService.requestVacation(request.getMemberId(), date);
    }
}
