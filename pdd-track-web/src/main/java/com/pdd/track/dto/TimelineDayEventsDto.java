package com.pdd.track.dto;

import lombok.Data;

@Data
public class TimelineDayEventsDto {
    private boolean lecture;
    private DrivingDto driving;
    private DrivingDto additionalDriving;
    private boolean study;
    private TestingDto testing;
}
