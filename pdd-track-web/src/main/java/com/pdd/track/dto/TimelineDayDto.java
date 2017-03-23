package com.pdd.track.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TimelineDayDto {
    private int dayIndex;
    private LocalDate dayDate;
    private TimelineDayEventsDto dayEvents;
    private List<TimeLineDayHintDto> dayHints;
    private boolean weekend;
    private boolean today;
}
