package com.group.commutesystem.dto.member.response;

public class OverWorkResponse {
    private static long lastid =1;
    private long id;
    private String name;
    private long overtimeMinutes;

    public OverWorkResponse(String name,long overtimeMinutes) {
        this.id = lastid++;
        this.name = name;
        this.overtimeMinutes = overtimeMinutes;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getOvertimeMinutes() {
        return overtimeMinutes;
    }
}
