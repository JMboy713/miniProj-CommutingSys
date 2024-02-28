package com.group.commutesystem.model.team;

import com.group.commutesystem.model.member.Member;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    private String name;

    protected Team() {
    }

    public Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getManager(){
        var manager = members.stream().filter(Member::isRole).findFirst().orElse(null);
        return manager == null? null : manager.getName();
    }

    public int getMemberCount(){
        return members.size();
    }
}
