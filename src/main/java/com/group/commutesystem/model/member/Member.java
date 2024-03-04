package com.group.commutesystem.model.member;

import com.group.commutesystem.dto.member.request.CreateMemberRequest;
import com.group.commutesystem.model.member.commute.Commute;
import com.group.commutesystem.model.member.vacation.Vacation;
import com.group.commutesystem.model.team.Team;
import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;
    @Column(nullable = false, length = 50)
    private String name;

    @JoinColumn(name = "team_name")
    @ManyToOne
    private Team team;
    private boolean role;
    private LocalDate birthday;

    private LocalDate workStartDate;

    @OneToMany(mappedBy = "member")
    private List<Commute> histories = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Vacation> vacations = new ArrayList<>();






    protected Member() {
    }

    public Member(CreateMemberRequest request, Team team) {
        if(request.isRole()==true &&team.getManager()!=null){
            throw new IllegalArgumentException("매니저가 이미 있습니다.");
        }

        this.name = request.getName();
        this.team = team;
        this.role = request.isRole();
        this.birthday = request.getBirthday();
        this.workStartDate = request.getWorkStartDate();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
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
