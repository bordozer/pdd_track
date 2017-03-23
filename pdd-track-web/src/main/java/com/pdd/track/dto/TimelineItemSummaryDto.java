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
    private TimelineItemSummaryStatus timelineItemSummaryStatus;

    public enum TimelineItemSummaryStatus {
        NONE,
        COMPLETELY_READY,
        READY_WITH_RISK,
        NOT_READY,
        TO_STUDY,
        NO_LECTURE_YET
    }
}
