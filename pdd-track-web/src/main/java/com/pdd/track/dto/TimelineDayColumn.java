package com.pdd.track.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TimelineDayColumn {
    private int dayIndex;
    private LocalDate date;
    private TimelineDayColumnEventsDto columnEvents;
}
