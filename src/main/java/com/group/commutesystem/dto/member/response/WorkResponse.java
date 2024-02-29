package com.group.commutesystem.dto.member.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkResponse {
    private List<WorkDetail> details;
    private long sum;

    public WorkResponse(List<WorkDetail> details, long sum) {
        this.details = details;
        this.sum = sum;
    }

//    public WorkResponse() {
//        this.details = new ArrayList<>();
//        this.sum = 0;
//    }

    public void addDetails(LocalDate date, long workingMinutes) {
        this.details.add(new WorkDetail(date,workingMinutes));
        this.sum += workingMinutes;
    }

    public static class WorkDetail{
        private LocalDate date;
        private long workingMinutes;

        public WorkDetail(LocalDate date, long workingMinutes) {
            this.date = date;
            this.workingMinutes = workingMinutes;
        }

        public LocalDate getDate() {
            return date;
        }

        public long getWorkingMinutes() {
            return workingMinutes;
        }
    }

    public List<WorkDetail> getDetails() {
        return details;
    }

    public long getSum() {
        return sum;
    }
}
