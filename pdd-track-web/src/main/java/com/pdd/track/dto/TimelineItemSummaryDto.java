package com.pdd.track.dto;

import lombok.Data;

@Data
public class TimelineItemSummaryDto {
    private int testsCount;
    private int testsAveragePercentage;
    private boolean lecture;
    private boolean study;
    private boolean greenStatus;
    private boolean studySuccess;
}
