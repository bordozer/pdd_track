package com.pdd.track.dto;

import lombok.Data;

@Data
public class TimelineDayEventsDto {
    private boolean lecture;
    private boolean study;
    private TestingDto testing;
    private TimelineDayStatus dayStatus;

    public enum TimelineDayStatus {
        NO_STUDYING,
        NO_STUDYING_BUT_NO_STUDY,
        NO_STUDYING_AND_STUDY
    }
}
