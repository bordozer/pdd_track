package com.pdd.track.dto;

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

    @Data
    public static class TimelineStatistics {
        private final List<SectionDataHolder> holders = new ArrayList<>();

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
