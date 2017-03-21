package com.pdd.track.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TimelineDayDto {
    private int dayIndex;
    private LocalDate dayDate;
    private TimelineDayEventsDto dayEvents;
}
