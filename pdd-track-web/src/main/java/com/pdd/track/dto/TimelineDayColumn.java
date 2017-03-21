package com.pdd.track.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TimelineDayColumn {
    private int index;
    private LocalDate date;
//    private boolean dayPassed;
//    private boolean redQuestionsDay;
}
