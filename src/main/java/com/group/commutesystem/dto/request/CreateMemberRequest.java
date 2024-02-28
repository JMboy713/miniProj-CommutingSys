package com.group.commutesystem.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

public class CreateMemberRequest {
    private String name;
    private String teamName;
    private boolean role;
    private Date birthday;
    private Date workStartDate;

    public String getName() {
        return name;
    }

    public String getTeamName() {
        return teamName;
    }

    public boolean isRole() {
        return role;
    }

    public Date getBirthday() {
        return birthday;
    }

    public Date getWorkStartDate() {
        return workStartDate;
    }
}
