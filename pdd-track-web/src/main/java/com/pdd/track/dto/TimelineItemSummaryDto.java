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
    private TimelineItemSummaryStatus timelineItemSummaryStatus;

    public enum TimelineItemSummaryStatus {
        NONE,
        READY,
        UNDER_THE_RISK,
        NOT_READY
    }
}
