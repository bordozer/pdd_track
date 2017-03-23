package com.pdd.track.dto;

import com.pdd.track.model.TimeLineDayHintType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeLineDayHintDto {
    private TimeLineDayHintType dayHintType;
    private long ageInDays;
    private long questionsCount;
}
