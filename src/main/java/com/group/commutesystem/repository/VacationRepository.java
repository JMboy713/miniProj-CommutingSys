package com.group.commutesystem.repository;

import com.group.commutesystem.model.member.vacation.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VacationRepository extends JpaRepository<Vacation, Long>{
    List<Vacation> findAllByMemberId(Long memberid);
}
