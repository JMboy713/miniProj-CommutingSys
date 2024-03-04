package com.group.commutesystem.dto.member.request;

import java.util.Date;

public class CreateVacationRequest {
    private Long memberId;
    private String date;

    public CreateVacationRequest(Long memberId, String date) {
        this.memberId = memberId;
        this.date = date;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getDate() {
        return date;
    }
}
