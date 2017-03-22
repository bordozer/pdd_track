package com.pdd.track.dto;

import lombok.Data;

@Data
public class TimelineDayColumnEventsDto {
    private boolean dayPassed;
    private boolean today;
    private DrivingDto driving;
    private DrivingDto additionalDriving;
//    private boolean redQuestionsDay;
}
