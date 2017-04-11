package com.pdd.track.converter;

import com.google.common.collect.Lists;
import com.pdd.track.dto.TimelineDayColumnEventsDto;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.dto.TimelineDto.SectionDataHolder;
import com.pdd.track.dto.TimelineDto.TimelineStatistics;
import com.pdd.track.dto.TimelineItemDto;
import com.pdd.track.dto.TimelineItemSummaryDto.TimelineItemSummaryStatus;
import com.pdd.track.utils.CommonUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimelineStatisticsConverter {


    public static TimelineStatistics convertStatistics(final TimelineDto timeline) {
        TimelineStatistics statistics = new TimelineStatistics();

        List<TimelineItemDto> totalSections = timeline.getItems().stream().collect(Collectors.toList());
        int totalSectionsCount = totalSections.size();
        int totalQuestionsCount = totalSections.stream().mapToInt(item -> item.getPddSection().getQuestionsCount()).sum();
        SectionDataHolder sectionDataHolder = new SectionDataHolder("Section total", totalSectionsCount, CommonUtils.formatDouble(100), totalQuestionsCount, CommonUtils.formatDouble(100));
        statistics.add(sectionDataHolder);

        List<TimelineItemDto> lectureSections = timeline.getItems().stream().filter(item -> item.getTimelineItemSummary().isLecture()).collect(Collectors.toList());
        int lectureSectionsCount = lectureSections.size();
        int lectureQuestionsCount = lectureSections.stream().mapToInt(item -> item.getPddSection().getQuestionsCount()).sum();
        String lectureSectionsPercentage = CommonUtils.formatDouble((double) lectureSectionsCount / totalSectionsCount * 100);
        String lectureQuestionsPercentage = CommonUtils.formatDouble((double) lectureQuestionsCount / totalQuestionsCount * 100);
        statistics.add(new SectionDataHolder("Lectured", lectureSectionsCount, lectureSectionsPercentage, lectureQuestionsCount, lectureQuestionsPercentage));

        List<TimelineItemDto> studySections = timeline.getItems().stream().filter(item -> item.getTimelineItemSummary().isStudy()).collect(Collectors.toList());
        int studySectionsCount = studySections.size();
        int studyQuestionsCount = studySections.stream().mapToInt(item -> item.getPddSection().getQuestionsCount()).sum();
        String studySectionsPercentage = CommonUtils.formatDouble((double) studySectionsCount / totalSectionsCount * 100);
        String studyQuestionsPercentage = CommonUtils.formatDouble((double) studyQuestionsCount / totalQuestionsCount * 100);
        statistics.add(new SectionDataHolder("Study", studySectionsCount, studySectionsPercentage, studyQuestionsCount, studyQuestionsPercentage));

        statistics.add(getSectionDataHolder(timeline, "Not studied or red tests", totalSectionsCount, totalQuestionsCount, TimelineItemSummaryStatus.NEED_MORE_TESTING, TimelineItemSummaryStatus.NEED_MORE_TESTING, TimelineItemSummaryStatus.TO_STUDY));
        statistics.add(getSectionDataHolder(timeline, "Ready with risks", totalSectionsCount, totalQuestionsCount, TimelineItemSummaryStatus.READY_WITH_RISK));
        statistics.add(getSectionDataHolder(timeline, "Completely ready", totalSectionsCount, totalQuestionsCount, TimelineItemSummaryStatus.COMPLETELY_READY));
        statistics.add(getSectionDataHolder(timeline, "Ready or ready with risks", totalSectionsCount, totalQuestionsCount, TimelineItemSummaryStatus.COMPLETELY_READY, TimelineItemSummaryStatus.READY_WITH_RISK));

        int schoolDriving = timeline.getDayColumns().stream()
                .mapToInt(day -> {
                    TimelineDayColumnEventsDto columnEvents = day.getColumnEvents();
                    if (columnEvents.getDriving() != null) {
                        return columnEvents.getDriving().getDuration();
                    }
                    return 0;
                }).sum();
        statistics.setSchoolDriving(CommonUtils.formatDouble((double) schoolDriving / 60));

        int additionalDriving = timeline.getDayColumns().stream()
                .mapToInt(day -> {
                    TimelineDayColumnEventsDto columnEvents = day.getColumnEvents();
                    if (columnEvents.getAdditionalDriving() != null) {
                        return columnEvents.getAdditionalDriving().getDuration();
                    }
                    return 0;
                }).sum();
        statistics.setAdditionalDriving(CommonUtils.formatDouble((double) additionalDriving / 60));
        statistics.setTotalDriving(CommonUtils.formatDouble((double) schoolDriving / 60 + (double) additionalDriving / 60));

        return statistics;
    }

    private static SectionDataHolder getSectionDataHolder(final TimelineDto result, final String title, final int totalSectionsCount, final int totalQuestionsCount, final TimelineItemSummaryStatus... summaryStatuses) {
        List<TimelineItemDto> sections = result.getItems().stream().filter(item -> Lists.newArrayList(summaryStatuses).contains(item.getTimelineItemSummary().getTimelineItemSummaryStatus())).collect(Collectors.toList());
        int sectionsCount = sections.size();
        int questionsCount = sections.stream().mapToInt(item -> item.getPddSection().getQuestionsCount()).sum();
        String sectionsPercentage = CommonUtils.formatDouble((double) sectionsCount / totalSectionsCount * 100);
        String questionsPercentage = CommonUtils.formatDouble((double) questionsCount / totalQuestionsCount * 100);
        return new SectionDataHolder(title, sectionsCount, sectionsPercentage, questionsCount, questionsPercentage);
    }
}
