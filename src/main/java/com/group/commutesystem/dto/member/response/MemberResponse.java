package com.group.commutesystem.dto.member.response;

import com.group.commutesystem.model.team.Team;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;

public class MemberResponse {
    private String name;
    private String teamName;
    private boolean role;
    private LocalDate birthday;
    private LocalDate workStartDate;

    public MemberResponse(String name, String teamName, boolean role, LocalDate birthday, LocalDate workStartDate) {
        this.name = name;
        this.teamName = teamName;
        this.role = role;
        this.birthday = birthday;
        this.workStartDate = workStartDate;
    }

    public String getName() {
        return name;
    }

    public String getTeamName() {
        return teamName;
    }

    public boolean isRole() {
        return role;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public LocalDate getWorkStartDate() {
        return workStartDate;
    }
}
