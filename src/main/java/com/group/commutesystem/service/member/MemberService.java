package com.group.commutesystem.service.member;

import com.group.commutesystem.dto.member.request.CreateMemberRequest;
import com.group.commutesystem.dto.member.response.MemberResponse;
import com.group.commutesystem.dto.member.response.VacationResponse;
import com.group.commutesystem.dto.member.response.WorkResponse;
import com.group.commutesystem.model.member.Member;
import com.group.commutesystem.model.member.commute.Commute;
import com.group.commutesystem.model.member.vacation.Vacation;
import com.group.commutesystem.repository.MemberCommuteHistoryRepository;
import com.group.commutesystem.repository.MemberRepository;
import com.group.commutesystem.model.team.Team;
import com.group.commutesystem.repository.TeamRepository;
import com.group.commutesystem.repository.VacationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final MemberCommuteHistoryRepository memberCommuteHistoryRepository;
    private final VacationRepository vacationRepository;


    public MemberService(MemberRepository memberRepository, TeamRepository teamRepository, MemberCommuteHistoryRepository memberCommuteHistoryRepository, VacationRepository vacationRepository) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
        this.memberCommuteHistoryRepository = memberCommuteHistoryRepository;
        this.vacationRepository = vacationRepository;
    }

    @Transactional
    public void createMember(CreateMemberRequest createMemberRequest) {
        Team team = teamRepository.findByName(createMemberRequest.getTeamName())
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));
        Member member = new Member(createMemberRequest, team);
        memberRepository.save(member);
    }
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(member -> new MemberResponse(member.getName(), member.getTeam().getName(), member.isRole(), member.getBirthday(), member.getWorkStartDate())).collect(Collectors.toList());

    }

    @Transactional
    public void goToWork(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 사원입니다."));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 오늘 날짜에 해당하는 출근 기록 조회
        List<Commute> existingHistory = memberCommuteHistoryRepository.findByMemberIdAndDate(memberId, today);
        if (!existingHistory.isEmpty()) {
            // 마지막 최근의 history를 가져온다.
            Commute history = existingHistory.get(existingHistory.size() - 1);

            // 이미 출근 기록이 있고, 퇴근 시간이 설정되지 않은 경우 에러 반환
            if (history.getStartTime() != null && history.getEndTime() == null) {
                throw new IllegalStateException("이미 출근한 사원입니다.");
            }
            // 같은 날짜에 출근했다가 퇴근한 기록이 있는 경우, 새로운 기록 추가
            else if (history.getEndTime() != null) {
                Commute newHistory = new Commute(member, today, now, null);
                memberCommuteHistoryRepository.save(newHistory);
            }
        } else {
            // 오늘 날짜에 해당하는 출근 기록이 없는 경우 새 기록 생성 및 저장
            Commute newHistory = new Commute(member, today, now, null);
            memberCommuteHistoryRepository.save(newHistory);
        }
//        MemberCommuteHistory history = MemberCommuteHistoryRepository.findByMemberIdAndDate(member.getId(), LocalDate.now())
//                .orElse(new MemberCommuteHistory(member, LocalDate.now(), LocalTime.now(),LocalTime.now()));
    }

    @Transactional
    public void getOffWork(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 사원입니다."));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 오늘 날짜에 해당하는 출근 기록 조회
        List<Commute> existingHistory = memberCommuteHistoryRepository.findByMemberIdAndDate(memberId, today);

        if (!existingHistory.isEmpty()) {
            // 마지막 최근의 history를 가져온다.
            Commute history = existingHistory.get(existingHistory.size() - 1);

            // 이미 출근 기록이 없고, 퇴근 시간이 설정되지 않은 경우 에러 반환
            if (history.getStartTime() != null && history.getEndTime() == null) {
                history.setEndTime(now);
            }
            // 같은 날짜에 출근했다가 퇴근한 기록이 있는 경우, 새로운 기록 추가
            else if (history.getEndTime() != null) {
                throw new IllegalStateException("이미 퇴근한 사원입니다.");
            }
        } else {
            // 오늘 날짜에 해당하는 출근 기록이 없는 경우 새 기록 생성 및 저장
            throw new IllegalStateException("출근 기록이 없습니다.");
        }
    }

    @Transactional
    public WorkResponse getCommuteHistory(Long memberId, YearMonth date) {
        LocalDate start = date.atDay(1);
        LocalDate end = date.atEndOfMonth();
        System.out.println(start+""+end);
        List<Commute> commutes = memberCommuteHistoryRepository.findByMemberIdAndDateBetween(memberId, start, end);
//        WorkResponse workResponse = new WorkResponse();
//
//        for (Commute commute : commutes) {
//            long workingMinutes = ChronoUnit.MINUTES.between(commute.getStartTime(), commute.getEndTime());
//            workResponse.addDetails(commute.getDate(), workingMinutes);
//            System.out.println(commute.getDate() + " " + workingMinutes);
//        }
//        return workResponse;
        Map<LocalDate, Long> dailyWorkMinutes = commutes.stream()
                .collect(Collectors.groupingBy(Commute::getDate,
                        Collectors.summingLong(commute ->
                                ChronoUnit.MINUTES.between(commute.getStartTime(), commute.getEndTime()))));

        // 모든 근무 시간의 총합 계산
        long totalWorkMinutes = dailyWorkMinutes.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        // 결과 구성 및 반환
        List<WorkResponse.WorkDetail> workDetails = dailyWorkMinutes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new WorkResponse.WorkDetail(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new WorkResponse(workDetails, totalWorkMinutes);



    }

    @Transactional
    public void requestVacation(Long memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 사원입니다."));


        int dueDate = member.getTeam().getDueDate(); // 휴가 전 duedate 전에 사용해야함.
        if (LocalDate.now().plusDays(dueDate).isAfter(date)) {// 오늘 + duedate 가 date 보다 뒤라면.
            throw new IllegalStateException("휴가 신청은 " + dueDate + "일 전에만 가능합니다.");
        }

        // 현재 연도에 해당하는 휴가만 필터링
        List<Vacation> vacations = vacationRepository.findAllByMemberId(memberId).stream()
                .filter(vacation -> vacation.getDate().getYear() == LocalDate.now().getYear())
                .collect(Collectors.toList());

        // 사용한 휴가 일수 계산
        long usedVacationDays = vacations.size(); // 여기서는 모든 휴가가 1일로 계산된다고 가정합니다.

        // 회원 등록 연도 확인
        boolean isNewMemberThisYear = member.getWorkStartDate().getYear() == LocalDate.now().getYear();

        // 휴가 승인 조건 검사
        if ((isNewMemberThisYear && usedVacationDays < 11) || (!isNewMemberThisYear && usedVacationDays < 15)) {
            // 휴가 승인 로직
            Vacation vacation = new Vacation(member, date);
            vacationRepository.save(vacation);
        } else {
            // 조건 불충족 시 예외 처리
            throw new IllegalStateException("휴가 사용 한도를 초과하였습니다.");
        }
    }

    public VacationResponse getVacation(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 사원입니다."));

        // 올해 사용한 vacations 가져옴.
        List<Vacation> vacations = vacationRepository.findAllByMemberId(memberId).stream()
                .filter(vacation -> vacation.getDate().getYear() == LocalDate.now().getYear())
                .collect(Collectors.toList());

        // 사용한 휴가 일수 계산
        long usedVacationDays = vacations.size(); // 여기서는 모든 휴가가 1일로 계산된다고 가정합니다.

        // 회원 등록 연도 확인
        boolean isNewMemberThisYear = member.getWorkStartDate().getYear() == LocalDate.now().getYear();

        if (isNewMemberThisYear ) {
            return new VacationResponse(11-usedVacationDays);
        }
        return new VacationResponse(15-usedVacationDays);
    }


}
