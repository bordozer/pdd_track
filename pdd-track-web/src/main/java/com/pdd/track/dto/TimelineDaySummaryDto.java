package com.pdd.track.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimelineDaySummaryDto {
    private double value;
    private String valueFormatted;
    private int potentialQuestionsCountToRepeat;
}
