package com.group.commutesystem.dto.team.response;

import com.group.commutesystem.model.team.Team;

public class TeamResponse {
    private String name;
    private String manager;
    private int memberCount;

    public TeamResponse(Team team) {
        this.name = team.getName();
        this.manager = team.getManager();
        this.memberCount = team.getMemberCount();
    }

    public String getName() {
        return name;
    }

    public String getManager() {
        return manager;
    }

    public int getMemberCount() {
        return memberCount;
    }
}
