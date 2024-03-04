package com.group.commutesystem.model.member.vacation;

import com.group.commutesystem.model.member.Member;
import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class Vacation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate date;
    public Vacation() {} // 기본 생성자.

    public Vacation(Member member, LocalDate date) {
        this.member = member;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getDate() {
        return date;
    }
}
