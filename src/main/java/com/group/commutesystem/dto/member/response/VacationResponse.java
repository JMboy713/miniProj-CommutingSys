package com.group.commutesystem.dto.member.response;

public class VacationResponse {
    private long leftVacation;

    public VacationResponse(long leftVacation) {
        this.leftVacation = leftVacation;
    }

    public long getLeftVacation() {
        return leftVacation;
    }
}
