package com.group.commutesystem.repository;

import com.group.commutesystem.model.member.commute.Commute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberCommuteHistoryRepository extends JpaRepository<Commute, Long> {
    Optional<Commute> findByMemberIdAndDate(Long memberId, LocalDate Date);


}
