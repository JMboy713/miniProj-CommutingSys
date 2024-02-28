package com.group.commutesystem.dto.response;

import java.util.Date;

public class MemberResponse {
    private String name;
    private String teamName;
    private boolean role;
    private Date birthday;
    private Date workStartDate;

    public MemberResponse(String name, String teamName, boolean role, Date birthday, Date workStartDate) {
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

    public Date getBirthday() {
        return birthday;
    }

    public Date getWorkStartDate() {
        return workStartDate;
    }
}
