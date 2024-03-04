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

//    public void addDetails(LocalDate date, long workingMinutes) {
//        this.details.add(new WorkDetail(date,workingMinutes,false));
//        this.sum += workingMinutes;
//    }

    public static class WorkDetail{
        private LocalDate date;
        private long workingMinutes;
        private Boolean usingDayOff;

        public WorkDetail(LocalDate date, long workingMinutes, Boolean usingDayOff) {
            this.date = date;
            this.workingMinutes = workingMinutes;
            this.usingDayOff = usingDayOff;
        }

        public LocalDate getDate() {
            return date;
        }

        public long getWorkingMinutes() {
            return workingMinutes;
        }

        public Boolean getUsingDayOff() {
            return usingDayOff;
        }

        public void setWorkingMinutes(long workingMinutes) {
            this.workingMinutes += workingMinutes;
        }

        public void setUsingDayOff(Boolean usingDayOff) {
            this.usingDayOff = usingDayOff;
        }
        public void setWorkMinutes(long workingMinutes) {
            this.workingMinutes += workingMinutes;
        }

    }

    public List<WorkDetail> getDetails() {
        return details;
    }

    public long getSum() {
        return sum;
    }
}
