package com.pdd.track.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
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
        private SectionDataHolder section;
        private SectionDataHolder sectionLectures;
        private SectionDataHolder sectionStudy;
        private SectionDataHolder sectionReady;
        private SectionDataHolder sectionReadyWithRisks;
        private SectionDataHolder sectionNotReady;
    }

    @Data
    @AllArgsConstructor
    public static class SectionDataHolder {
        private final int sectionsCount;
        private final int sectionsQuestionsCount;
        private final String percentage;
    }
}
