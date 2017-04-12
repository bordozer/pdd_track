package com.pdd.track.dto;

import com.pdd.track.utils.CommonUtils;
import lombok.Data;
import lombok.Getter;

@Data
public class TimelineItemSummaryDto {
    private int testsCount;
    private boolean lecture;
    private boolean study;

    private boolean lastTestSuccessful;
    private TestPercentageHolder averageTestingPercentage;
    private TestPercentageHolder lastTestingPercentage;

    private TimelineItemSummaryStatus timelineItemSummaryStatus;

    public enum TimelineItemSummaryStatus {
        NONE,
        COMPLETELY_READY,
        READY_WITH_RISK,
        NEED_MORE_TESTING,
        TESTS_ARE_RED,
        TO_STUDY,
        NO_LECTURE_YET
    }

    @Getter
    public static class TestPercentageHolder {
        private final double percentage;
        private final String percentageFormatted;

        public TestPercentageHolder(final double lastTestPercentage) {
            this.percentage = lastTestPercentage;
            this.percentageFormatted = CommonUtils.formatDouble(lastTestPercentage);
        }
    }
}
