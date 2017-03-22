package com.pdd.track.dto;

import lombok.Data;

@Data
public class TimelineItemSummaryDto {
    private int testsCount;
    private double testsAveragePercentage;
    private String testsAveragePercentageFormatted;
    private boolean lecture;
    private boolean study;
    private boolean lastTestSuccessful;
    private boolean studySuccess;
}
