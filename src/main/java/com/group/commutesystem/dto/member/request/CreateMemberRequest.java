package com.group.commutesystem.dto.member.request;

import com.group.commutesystem.model.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;

public class CreateMemberRequest {
    private String name;
    private String teamName;
    private boolean role;
    private LocalDate birthday;
    private LocalDate workStartDate;

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
