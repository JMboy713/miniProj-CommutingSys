package com.group.commutesystem.model.member;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.*;

public interface MemberRepository extends JpaRepository<Member, Long>{

}
