package com.group.commutesystem.repository;

import com.group.commutesystem.model.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.*;

public interface MemberRepository extends JpaRepository<Member, Long>{

}
