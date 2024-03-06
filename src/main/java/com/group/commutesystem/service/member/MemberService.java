package com.group.commutesystem.service.member;

import com.group.commutesystem.dto.member.request.CreateMemberRequest;
import com.group.commutesystem.dto.member.response.MemberResponse;
import com.group.commutesystem.dto.member.response.OverWorkResponse;
import com.group.commutesystem.dto.member.response.VacationResponse;
import com.group.commutesystem.dto.member.response.WorkResponse;
import com.group.commutesystem.model.member.Member;
import com.group.commutesystem.model.member.commute.Commute;
import com.group.commutesystem.model.member.vacation.Vacation;
import com.group.commutesystem.model.team.Team;
import com.group.commutesystem.repository.MemberCommuteHistoryRepository;
import com.group.commutesystem.repository.MemberRepository;
import com.group.commutesystem.repository.TeamRepository;
import com.group.commutesystem.repository.VacationRepository;
import com.ibm.icu.util.ChineseCalendar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
    public void getOffWork(Long memberId) {
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
    public WorkResponse getCommuteHistory(Long memberId, YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        System.out.println(start + "" + end);
        List<Commute> commutes = memberCommuteHistoryRepository.findByMemberIdAndDateBetween(memberId, start, end);
//        WorkResponse workResponse = new WorkResponse();
//
//        for (Commute commute : commutes) {
//            long workingMinutes = ChronoUnit.MINUTES.between(commute.getStartTime(), commute.getEndTime());
//            workResponse.addDetails(commute.getDate(), workingMinutes);
//            System.out.println(commute.getDate() + " " + workingMinutes);
//        }
//        return workResponse;
        // 1일부터 31일까지의 날짜를 다 가져옴.
        Map<LocalDate, WorkResponse.WorkDetail> workDetailsMap = start.datesUntil(end.plusDays(1))
                .collect(Collectors.toMap(date -> date, date -> new WorkResponse.WorkDetail(date, 0L, false)));

        // 출근 기록 조회 및 처리
        commutes.forEach(commute -> {
            LocalDate date = commute.getDate();
            long minutes = ChronoUnit.MINUTES.between(commute.getStartTime(), commute.getEndTime());
            WorkResponse.WorkDetail detail = workDetailsMap.get(date);
            detail.setWorkingMinutes(minutes); // 출근한 경우 근무 시간 업데이트
        });

        // 휴가 기록 조회 및 처리
        List<Vacation> vacations = vacationRepository.findAllByMemberId(memberId);
        vacations.forEach(vacation -> {
            LocalDate date = vacation.getDate();
            if (workDetailsMap.containsKey(date)) {
                WorkResponse.WorkDetail detail = workDetailsMap.get(date);
                detail.setUsingDayOff(true);
                detail.setWorkMinutes(0); // 휴가 사용한 경우 근무 시간을 0으로 설정
            }
        });

        // 최종 WorkResponse 구성
        List<WorkResponse.WorkDetail> workDetails = new ArrayList<>(workDetailsMap.values());
        workDetails.sort(Comparator.comparing(WorkResponse.WorkDetail::getDate)); // 날짜 순으로 정렬

        long totalWorkMinutes = workDetails.stream()
                .mapToLong(WorkResponse.WorkDetail::getWorkingMinutes)
                .sum();

        return new WorkResponse(workDetails, totalWorkMinutes);


//        Map<LocalDate, Long> dailyWorkMinutes = commutes.stream()
//                .collect(Collectors.groupingBy(Commute::getDate,
//                        Collectors.summingLong(commute ->
//                                ChronoUnit.MINUTES.between(commute.getStartTime(), commute.getEndTime()))));
//
//        // 모든 근무 시간의 총합 계산
//        long totalWorkMinutes = dailyWorkMinutes.values().stream()
//                .mapToLong(Long::longValue)
//                .sum();
//
//        // 결과 구성 및 반환
////        List<WorkResponse.WorkDetail> workDetails = dailyWorkMinutes.entrySet().stream()
////                .sorted(Map.Entry.comparingByKey())
////                .map(entry -> new WorkResponse.WorkDetail(entry.getKey(), entry.getValue()))
////                .collect(Collectors.toList());
//
//        // 특정 멤버의 휴가 데이터 조회
//        List<Vacation> vacations = vacationRepository.findAllByMemberId(memberId);
//
//// 조회된 휴가 데이터를 기반으로 날짜별 휴가 사용 여부 맵 생성
//        Map<LocalDate, Boolean> vacationUsageMap = vacations.stream()
//                .collect(Collectors.toMap(Vacation::getDate, v -> true, (existing, replacement) -> existing));
//
//// dailyWorkMinutes와 vacationUsageMap을 이용하여 WorkDetail 리스트 생성
//        List<WorkResponse.WorkDetail> workDetails = dailyWorkMinutes.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey())
//                .map(entry -> {
//                    // 해당 날짜에 휴가를 사용했는지 확인
//                    boolean vacationUsed = vacationUsageMap.getOrDefault(entry.getKey(), false);
//
//                    // 근무 시간을 휴가 사용 여부에 따라 조정
//                    long workMinutes = vacationUsed ? 0 : entry.getValue();
//
//                    // WorkDetail 객체 생성
//                    return new WorkResponse.WorkDetail(entry.getKey(), workMinutes, vacationUsed);
//                })
//                .collect(Collectors.toList());
//
//        return new WorkResponse(workDetails, totalWorkMinutes);


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

        if (isNewMemberThisYear) {
            return new VacationResponse(11 - usedVacationDays);
        }
        return new VacationResponse(15 - usedVacationDays);
    }


    public List<OverWorkResponse> getOverWorking(YearMonth yearMonth) {
        List<Member> members = memberRepository.findAll();
        List<OverWorkResponse> overWorkResponses = new ArrayList<>();
        LunarCalendar lunarCalendar = new LunarCalendar();
        long workingMinutes = lunarCalendar.calculateMonthlyWorkHours(yearMonth.getYear(), yearMonth.getMonthValue()) ;

        for (Member member : members) {
            LocalDate start = yearMonth.atDay(1);
            LocalDate end = yearMonth.atEndOfMonth();
            // 해당 멤버의 출퇴근 기록 조회
            List<Commute> commutes = memberCommuteHistoryRepository.findByMemberIdAndDateBetween(member.getId(), start, end);
            // 총 근무 시간
            long totalWorkMinutes = commutes.stream()
                    .mapToLong(commute -> ChronoUnit.MINUTES.between(commute.getStartTime(), commute.getEndTime()))
                    .sum();
            long overWorkMinutes = totalWorkMinutes - workingMinutes ;
            System.out.println(totalWorkMinutes);
            System.out.println(workingMinutes);
            if (overWorkMinutes > 0) {
                overWorkResponses.add(new OverWorkResponse(member.getName(), overWorkMinutes));
            } else {
                overWorkResponses.add(new OverWorkResponse(member.getName(), 0));
            }
        }
        return overWorkResponses;
    }


}


class LunarCalendar {
    public static final int LD_SUNDAY = 7;
    public static final int LD_SATURDAY = 6;
    public static final int LD_MONDAY = 1;
    static Map<Integer, Set<LocalDate>> map = new HashMap<>();

    private LocalDate Lunar2Solar(LocalDate lunar) {
        ChineseCalendar cc = new ChineseCalendar();

        cc.set(ChineseCalendar.EXTENDED_YEAR, lunar.getYear() + 2637);   // 년, year + 2637
        cc.set(ChineseCalendar.MONTH, lunar.getMonthValue() - 1);        // 월, month -1
        cc.set(ChineseCalendar.DAY_OF_MONTH, lunar.getDayOfMonth());     // 일

        LocalDate solar = Instant.ofEpochMilli(cc.getTimeInMillis()).atZone(ZoneId.of("UTC")).toLocalDate();

        return solar;
    }

    /**
     * Return the set of holidays of input yaer
     *
     * <p>results of this method would be <i>saved</i> in static field {@code this.map}
     * after the method calculate holidays of input year
     *
     * @param year target year
     * @return set of holidays of input year
     */
    public Set<LocalDate> holidaySet(int year) {
        if (map.containsKey(year)) return map.get(year);
        Set<LocalDate> holidaysSet = new HashSet<>();

        // 양력 휴일
        holidaysSet.add(LocalDate.of(year, 1, 1));   // 신정
        holidaysSet.add(LocalDate.of(year, 3, 1));   // 삼일절
        holidaysSet.add(LocalDate.of(year, 5, 5));   // 어린이날
        holidaysSet.add(LocalDate.of(year, 6, 6));   // 현충일
        holidaysSet.add(LocalDate.of(year, 8, 15));   // 광복절
        holidaysSet.add(LocalDate.of(year, 10, 3));   // 개천절
        holidaysSet.add(LocalDate.of(year, 10, 9));   // 한글날
        holidaysSet.add(LocalDate.of(year, 12, 25));   // 성탄절

        // 음력 휴일
        holidaysSet.add(Lunar2Solar(LocalDate.of(year, 1, 1)).minusDays(1));  // ""
        holidaysSet.add(Lunar2Solar(LocalDate.of(year, 1, 1)));  // 설날
        holidaysSet.add(Lunar2Solar(LocalDate.of(year, 1, 2)));  // ""
        holidaysSet.add(Lunar2Solar(LocalDate.of(year, 4, 8)));  // 석탄일
        holidaysSet.add(Lunar2Solar(LocalDate.of(year, 8, 14)));  // ""
        holidaysSet.add(Lunar2Solar(LocalDate.of(year, 8, 15)));  // 추석
        holidaysSet.add(Lunar2Solar(LocalDate.of(year, 8, 16)));  // ""

        try {
            // 어린이날 대체공휴일 검사 : 어린이날은 토요일, 일요일인 경우 그 다음 평일을 대체공유일로 지정
            holidaysSet.add(substituteHoliday(LocalDate.of(year, 5, 5)));
            // 삼일절, 광복절, 개천절, 한글날
            holidaysSet.add(substituteHoliday(LocalDate.of(year, 3, 1)));
            holidaysSet.add(substituteHoliday(LocalDate.of(year, 8, 15)));
            holidaysSet.add(substituteHoliday(LocalDate.of(year, 10, 3)));
            holidaysSet.add(substituteHoliday(LocalDate.of(year, 10, 9)));


            // 설날 대체공휴일 검사
            if (Lunar2Solar(LocalDate.of(year, 1, 1)).getDayOfWeek().getValue() == LD_SUNDAY) {    // 일
                holidaysSet.add(Lunar2Solar(LocalDate.of(year, 1, 3)));
            }
            if (Lunar2Solar(LocalDate.of(year, 1, 1)).getDayOfWeek().getValue() == LD_MONDAY) {    // 월
                holidaysSet.add(Lunar2Solar(LocalDate.of(year, 1, 3)));
            }
            if (Lunar2Solar(LocalDate.of(year, 1, 2)).getDayOfWeek().getValue() == LD_SUNDAY) {    // 일
                holidaysSet.add(Lunar2Solar(LocalDate.of(year, 1, 3)));
            }

            // 추석 대체공휴일 검사
            if (Lunar2Solar(LocalDate.of(year, 8, 14)).getDayOfWeek().getValue() == LD_SUNDAY) {
                holidaysSet.add(Lunar2Solar(LocalDate.of(year, 8, 17)));
            }
            if (Lunar2Solar(LocalDate.of(year, 8, 15)).getDayOfWeek().getValue() == LD_SUNDAY) {
                holidaysSet.add(Lunar2Solar(LocalDate.of(year, 8, 17)));
            }
            if (Lunar2Solar(LocalDate.of(year, 8, 16)).getDayOfWeek().getValue() == LD_SUNDAY) {
                holidaysSet.add(Lunar2Solar(LocalDate.of(year, 8, 17)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put(year, holidaysSet);
        return holidaysSet;
    }

    /**
     * @param h holiday target of substitute holiday
     * @return new LocalDate Object: substitute holiday of input value
     */
    private LocalDate substituteHoliday(LocalDate h) {
        if (h.getDayOfWeek().getValue() == LD_SUNDAY) {      // 일요일
            return h.plusDays(1);
        }
        if (h.getDayOfWeek().getValue() == LD_SATURDAY) {  // 토요일
            return h.plusDays(2);
        }
        return h;
    }
    // 기존의 LunarCalendar 클래스 구현...

    /**
     * 한달 기준 근로 시간을 계산합니다.
     *
     * @param year 대상 연도
     * @param month 대상 월
     * @return 한달의 총 근로 시간 (분 단위)
     */
    public long calculateMonthlyWorkHours(int year, int month) {
        Set<LocalDate> holidays = holidaySet(year); // 해당 연도의 공휴일 셋을 가져옵니다.
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        long workDays = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            // 주말이 아니고, 공휴일이 아닌 날짜를 카운트합니다.
            if (!(date.getDayOfWeek().getValue() == LD_SATURDAY || date.getDayOfWeek().getValue() == LD_SUNDAY || holidays.contains(date))) {
                workDays++;
            }
        }

        long dailyWorkHours = 8; // 하루 근무 시간
        return workDays * dailyWorkHours * 60; // 총 근로 시간을 분 단위로 반환
    }

    // holidaySet, substituteHoliday 메서드와 나머지 LunarCalendar 클래스 구현...
}

