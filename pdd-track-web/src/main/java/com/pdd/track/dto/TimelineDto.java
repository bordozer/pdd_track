package com.pdd.track.dto;

import com.pdd.track.dto.TimelineItemSummaryDto.TestPercentageHolder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class TimelineDto {
    private List<TimelineDayColumn> dayColumns;
    private List<TimelineItemDto> items;
    private List<TimelineDaySummaryDto> summaryColumns;
    private LocalDate startDate;
    private LocalDate endDate;

    private TimelineStatistics timelineStatistics;

    private TestPercentageHolder averageTestingPercentageAvg;
    private TestPercentageHolder lastTestingPercentageAvg;
    private int totalTestingCount;

    @Data
    public static class TimelineStatistics {
        private final List<SectionDataHolder> holders = new ArrayList<>();

        private String schoolDriving;
        private String additionalDriving;
        private String totalDriving;

        public void add(final SectionDataHolder holder) {
            this.holders.add(holder);
        }
    }

    @Data
    @AllArgsConstructor
    public static class SectionDataHolder {
        private final String title;
        private final int sectionsCount;
        private final String sectionsPercentage;
        private final int sectionsQuestionsCount;
        private final String questionPercentage;
    }
}
