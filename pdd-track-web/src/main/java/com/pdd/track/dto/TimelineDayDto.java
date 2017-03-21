package com.pdd.track.dto;

import lombok.Data;

@Data
public class TimelineDayDto {
    private int dayIndex;
    private TimelineDayEventsDto cellEvents;
}
