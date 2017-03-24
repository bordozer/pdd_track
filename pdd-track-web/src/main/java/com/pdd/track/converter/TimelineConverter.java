package com.pdd.track.converter;

import com.pdd.track.dto.DrivingDto;
import com.pdd.track.dto.DrivingDto.CarDto;
import com.pdd.track.dto.DrivingDto.InstructorDto;
import com.pdd.track.dto.PddSectionDto;
import com.pdd.track.dto.TimeLineDayHintDto;
import com.pdd.track.dto.TimelineDayColumn;
import com.pdd.track.dto.TimelineDayColumnEventsDto;
import com.pdd.track.dto.TimelineDayDto;
import com.pdd.track.dto.TimelineDayEventsDto;
import com.pdd.track.dto.TimelineDaySummaryDto;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.dto.TimelineItemDto;
import com.pdd.track.dto.TimelineItemSummaryDto;
import com.pdd.track.dto.TimelineItemSummaryDto.TimelineItemSummaryStatus;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.PddSectionTimeline;
import com.pdd.track.model.PddSectionTimelineItem;
import com.pdd.track.model.SchoolTimeline;
import com.pdd.track.model.TimeLineDayHintType;
import com.pdd.track.model.TimeLineItemEventType;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.events.AbstractDrivingEvent;
import com.pdd.track.model.events.AbstractLectureEvent;
import com.pdd.track.model.events.AdditionalDrivingEvent;
import com.pdd.track.model.events.PddSectionTesting;
import com.pdd.track.model.events.TimelineEvent;
import com.pdd.track.service.impl.DataGenerationServiceImpl;
import com.pdd.track.utils.CommonUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimelineConverter {

    private static final int SECTION_TOO_LONG_WITHOUT_TESTING_DAYS = 5;
    private static final int COOL_SECTION_TOO_LONG_WITHOUT_TESTING_DAYS = 7;
    private static final int EXCELLENT_SECTION_TOO_LONG_WITHOUT_TESTING_DAYS = 10;

    private static final int SECTION_TOO_LONG_WITHOUT_RESTUDY_DAYS = 10;

    private static final int SECTION_TOO_LONG_WITHOUT_STUDY_DAYS = 5;
    private static final int MIN_TESTS_COUNT = 3;
    private static final EnumSet<DayOfWeek> WEEKENDS = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    public static final int GOOD_TEST_PERCENTAGE = 90;
    public static final int COLL_TEST_PERCENTAGE = 96;
    public static final int EXCELLENT_TEST_PERCENTAGE = 100;

    public static TimelineDto toDto(final List<PddSection> pddSections, final SchoolTimeline schoolTimeline,
                                    final PddSectionTimeline pddSectionTimeline, final LocalDate onDate) {
        TimelineDto result = new TimelineDto();
        result.setStartDate(DataGenerationServiceImpl.STUDY_START_DAY);
        result.setEndDate(DataGenerationServiceImpl.STUDY_END_DAY);

        List<TimelineItem> collect = schoolTimeline.getTimelineItems().stream()
                .filter(item -> item.getDate().equals(LocalDate.of(2017, 3, 23)))
                .collect(Collectors.toList());

        List<TimelineItem> schoolTimelineItems = schoolTimeline.getTimelineItems();
        List<PddSectionTimelineItem> pddSectionTimelineItems = pddSectionTimeline.getTimelineItems();

        List<TimelineDayColumn> dayColumns = getDayColumns(onDate);
        result.setDayColumns(dayColumns);
        dayColumns.stream()
                .forEach(dayColumn -> {
                    populateGlobalDayEvents(schoolTimelineItems, dayColumn);
                });

        result.setItems(convertTimelineItems(pddSections, schoolTimelineItems, pddSectionTimelineItems, dayColumns, onDate));
        result.setSummaryColumns(calculateTimelineDaySummary(pddSectionTimelineItems, dayColumns));
        result.setTimelineStatistics(TimelineStatisticsConverter.convertStatistics(result));

        return result;
    }

    private static List<TimelineItemDto> convertTimelineItems(final List<PddSection> pddSections, final List<TimelineItem> schoolTimelineItems, final List<PddSectionTimelineItem> pddSectionTimelineItems, final List<TimelineDayColumn> dayColumns, final LocalDate onDate) {
        List<TimelineItemDto> result = pddSections.stream()
                .map(section -> {
                    PddSectionDto sectionDto = TimelineObjectConverter.convertPddSection(section);
                    TimelineItemDto item1 = new TimelineItemDto();
                    item1.setPddSection(sectionDto);
                    item1.setTimelineDays(convertTimelineDaysForPddSection(sectionDto, pddSectionTimelineItems, dayColumns, onDate));
                    return item1;
                })
                .collect(Collectors.toList());
        populateLectureEvents(schoolTimelineItems, result);
        populateHintsOnDate(schoolTimelineItems, pddSectionTimelineItems, result, onDate);
        populateFutureHints(schoolTimelineItems, pddSectionTimelineItems, onDate, result);
        populateTimelineSummary(schoolTimelineItems, pddSectionTimelineItems, result, onDate);
        return result;
    }

    private static void populateLectureEvents(final List<TimelineItem> schoolTimelineItems, final List<TimelineItemDto> pddSectionTimelineItems) {
        pddSectionTimelineItems.stream()
                .forEach(tlsItem -> {
                    tlsItem.getTimelineDays().stream()
                            .forEach(tlDay -> {
                                String pddSectionKey = tlsItem.getPddSection().getKey();
                                tlDay.getDayEvents().setLecture(getLectureEventOnDate(pddSectionKey, schoolTimelineItems, tlDay, TimeLineItemEventType.LECTURE) != null);
                                tlDay.getDayEvents().setStudy(getLectureEventOnDate(pddSectionKey, schoolTimelineItems, tlDay, TimeLineItemEventType.LECTURE_STUDY) != null);
                            });
                });
    }

    private static TimelineItem getLectureEventOnDate(final String pddSectionKey, final List<TimelineItem> schoolTimelineItems, final TimelineDayDto tlDay,
                                                      final TimeLineItemEventType eventType) {
        return schoolTimelineItems.stream()
                .filter(stlItem -> {
                    if (!stlItem.getEvent().getEventType().equals(eventType)) {
                        return false;
                    }
                    if (!stlItem.getDate().equals(tlDay.getDayDate())) {
                        return false;
                    }
                    AbstractLectureEvent timelineLectureEvent = (AbstractLectureEvent) stlItem.getEvent();
                    return timelineLectureEvent.getPddSectionKey().equals(pddSectionKey);
                })
                .findFirst()
                .orElse(null);
    }

    private static void populateFutureHints(final List<TimelineItem> schoolTimelineItems, final List<PddSectionTimelineItem> pddSectionTimelineItems, final LocalDate onDate, final List<TimelineItemDto> result) {
        LocalDate aDate = onDate.plusDays(1);
        while (aDate.isBefore(DataGenerationServiceImpl.STUDY_END_DAY.plusDays(1))) {
            populateHintsOnDate(schoolTimelineItems, pddSectionTimelineItems, result, aDate);
            aDate = aDate.plusDays(1);
        }
    }

    private static List<TimelineDaySummaryDto> calculateTimelineDaySummary(final List<PddSectionTimelineItem> pddSectionTimelineItems, final List<TimelineDayColumn> dayColumns) {

        @Getter
        class QuestionsAggregator {
            private int value;

            public void add(final int value) {
                this.value += value;
            }
        }
        return dayColumns.stream()
                .map(dayColumn -> {
                    final ValuesAggregator valuesAggregator = new ValuesAggregator();
                    final QuestionsAggregator questionsAggregator = new QuestionsAggregator();
                    pddSectionTimelineItems.stream()
                            .forEach(item -> {
                                item.getTimelineItems().stream()
                                        .forEach(tlItem -> {
                                            if (!tlItem.getDate().equals(dayColumn.getDate())) {
                                                return;
                                            }
                                            if (tlItem.getEvent() == null || !TimeLineItemEventType.TESTING.equals(tlItem.getEvent().getEventType())) {
                                                return;
                                            }
                                            PddSectionTesting testingEvent = (PddSectionTesting) tlItem.getEvent();
                                            valuesAggregator.add(((double) testingEvent.getTesting().getPassedQuestions() / testingEvent.getTesting().getTotalQuestions()) * 100);
                                            questionsAggregator.add(0); // TODO: item.getPddSection().getQuestionsCount()
                                        });
                            });

                    if (valuesAggregator.getValue() == 0) {
                        return new TimelineDaySummaryDto(0, "", 0);
                    }
                    double value = valuesAggregator.getValue() / valuesAggregator.getCount();
                    return new TimelineDaySummaryDto(value, CommonUtils.formatDouble(value), questionsAggregator.getValue());
                })
                .collect(Collectors.toList());
    }

    private static ValuesAggregator calculateTimelinePddSectionSummary(final String pddSectionKey, final List<PddSectionTimelineItem> pddSectionTimelineItems) {
        final ValuesAggregator valuesAggregator = new ValuesAggregator();
        if (pddSectionTimelineItems == null) {
            return valuesAggregator;
        }
        pddSectionTimelineItems.stream()
                .forEach(item -> {
                    if (!item.getPddSection().getKey().equals(pddSectionKey)) {
                        return;
                    }
                    item.getTimelineItems().stream()
                            .forEach(tlItem -> {
                                if (tlItem.getEvent() == null || !TimeLineItemEventType.TESTING.equals(tlItem.getEvent().getEventType())) {
                                    return;
                                }
                                PddSectionTesting testingEvent = (PddSectionTesting) tlItem.getEvent();
                                valuesAggregator.add(((double) testingEvent.getTesting().getPassedQuestions() / testingEvent.getTesting().getTotalQuestions()) * 100);
                            });
                });
        return valuesAggregator;
    }

    private static List<TimelineDayColumn> getDayColumns(final LocalDate onDate) {
        int index = 1;
        LocalDate currentDay = DataGenerationServiceImpl.STUDY_START_DAY;
        List<TimelineDayColumn> result = new ArrayList<>();
        while (currentDay.isBefore(DataGenerationServiceImpl.STUDY_END_DAY.plusDays(1))) {
            TimelineDayColumn dayColumn = new TimelineDayColumn();
            dayColumn.setDayIndex(index);
            dayColumn.setDate(currentDay);

            TimelineDayColumnEventsDto columnEvents = new TimelineDayColumnEventsDto();
            if (currentDay.isBefore(onDate)) {
                columnEvents.setDayPassed(true);
            }
            if (currentDay.equals(onDate)) {
                columnEvents.setToday(true);
            }
            if (DataGenerationServiceImpl.LECTURE_WEEK_DAYS.contains(currentDay.getDayOfWeek())) {
                columnEvents.setLectureDay(true);
            }
            dayColumn.setColumnEvents(columnEvents);

            result.add(dayColumn);

            currentDay = currentDay.plusDays(1);
            index++;
        }
        return result;
    }

    private static void populateHintsOnDate(final List<TimelineItem> schoolTimelineItems, final List<PddSectionTimelineItem> pddSectionTimelineItems,
                                            final List<TimelineItemDto> visitor, final LocalDate onDate) {
        visitor.stream()
                .forEach(item -> {
                    item.getTimelineDays().stream()
                            .forEach(day -> {
                                if (!day.getDayDate().equals(onDate)) {
                                    return;
                                }
                                List<TimeLineDayHintDto> dayHints = new ArrayList<>();
                                day.setDayHints(dayHints);
                                String sectionKey = item.getPddSection().getKey();
                                int sessionQuestionCount = item.getPddSection().getQuestionsCount();
                                TimelineItem lastLectureStudyEvent = getLastLectureEvent(sectionKey, schoolTimelineItems, TimeLineItemEventType.LECTURE_STUDY);
                                if (lastLectureStudyEvent == null) {
                                    TimelineItem pddSectionLectureEvent = getLastLectureEvent(sectionKey, schoolTimelineItems, TimeLineItemEventType.LECTURE);
                                    if (pddSectionLectureEvent != null && CommonUtils.ageInDays(pddSectionLectureEvent.getDate(), onDate) > SECTION_TOO_LONG_WITHOUT_STUDY_DAYS) {
                                        dayHints.add(new TimeLineDayHintDto(TimeLineDayHintType.NEEDS_STUDY, CommonUtils.ageInDays(pddSectionLectureEvent.getDate(), onDate), sessionQuestionCount));
                                    }
                                } else {
                                    if (isSectionTooLongWithoutRestudy(sectionKey, schoolTimelineItems, onDate)) {
                                        dayHints.add(new TimeLineDayHintDto(TimeLineDayHintType.NEEDS_RESTUDY, CommonUtils.ageInDays(lastLectureStudyEvent.getDate(), onDate), sessionQuestionCount));
                                    }
                                }
                                TimelineItem lastTesting = getLastPddSectionTestingEvent(sectionKey, pddSectionTimelineItems, TimeLineItemEventType.TESTING);
                                if (lastTesting == null) {
                                    return;
                                }
                                if (isSectionTooLongWithoutTestsRepeating(sectionKey, pddSectionTimelineItems, onDate)) {
                                    dayHints.add(new TimeLineDayHintDto(TimeLineDayHintType.ADVICE_REFRESH_TESTS, CommonUtils.ageInDays(lastTesting.getDate(), onDate), sessionQuestionCount));
                                }
                                PddSectionTesting pddSectionTestingEvent = (PddSectionTesting) lastTesting.getEvent();
                                if (!pddSectionTestingEvent.getTesting().isPassed()) {
                                        dayHints.add(new TimeLineDayHintDto(TimeLineDayHintType.RED_TESTS, CommonUtils.ageInDays(lastTesting.getDate(), onDate), sessionQuestionCount));
                                }
                            });
                });
    }

    private static void populateTimelineSummary(final List<TimelineItem> schoolTimelineItems, final List<PddSectionTimelineItem> pddSectionTimelineItems,
                                                final List<TimelineItemDto> visitor, final LocalDate onDate) {
        visitor.stream()
                .forEach(item -> {
                    String sectionKey = item.getPddSection().getKey();
                    TimelineItem lastPddSectionLectureEvent = getLastLectureEvent(sectionKey, schoolTimelineItems, TimeLineItemEventType.LECTURE);
                    TimelineItem lastPddSectionLectureStudyEvent = getLastLectureEvent(sectionKey, schoolTimelineItems, TimeLineItemEventType.LECTURE_STUDY);

                    TimelineItemSummaryDto timelineItemSummary = new TimelineItemSummaryDto();
                    timelineItemSummary.setLecture(lastPddSectionLectureEvent != null);
                    timelineItemSummary.setStudy(lastPddSectionLectureStudyEvent != null);


                    boolean lastTestSuccessful = false;
                    TimelineItem lastPddSectionTesting = getLastPddSectionTestingEvent(sectionKey, pddSectionTimelineItems, TimeLineItemEventType.TESTING);
                    if (lastPddSectionTesting != null) {
                        PddSectionTesting lastSectionTesting = (PddSectionTesting) lastPddSectionTesting.getEvent();
                        lastTestSuccessful = lastSectionTesting.getTesting().isPassed();
                        timelineItemSummary.setLastTestSuccessful(lastTestSuccessful);
                    }

                    ValuesAggregator valuesAggregator = calculateTimelinePddSectionSummary(sectionKey, pddSectionTimelineItems);
                    double averageTestingScore = valuesAggregator.getValue() / valuesAggregator.getCount();
                    timelineItemSummary.setTestsCount(valuesAggregator.getCount());
                    timelineItemSummary.setTestsAveragePercentage(averageTestingScore);
                    timelineItemSummary.setTestsAveragePercentageFormatted(CommonUtils.formatDouble(averageTestingScore));
                    boolean testPercentageIsGood = averageTestingScore > GOOD_TEST_PERCENTAGE;

                    TimelineItemSummaryStatus pddSummaryStatus = TimelineItemSummaryStatus.NONE;
                    if (lastPddSectionLectureEvent != null && valuesAggregator.getCount() < MIN_TESTS_COUNT) {
                        pddSummaryStatus = TimelineItemSummaryStatus.NEED_MORE_TESTING;
                    }
                    if (lastPddSectionLectureEvent != null && lastPddSectionLectureStudyEvent == null) {
                        pddSummaryStatus = TimelineItemSummaryStatus.TO_STUDY;
                    }
                    if (valuesAggregator.getCount() >= MIN_TESTS_COUNT && testPercentageIsGood && lastTestSuccessful) {
                        if (isSectionTooLongWithoutTestsRepeating(sectionKey, pddSectionTimelineItems, onDate) || isSectionTooLongWithoutRestudy(sectionKey, schoolTimelineItems, onDate)) {
                            pddSummaryStatus = TimelineItemSummaryStatus.READY_WITH_RISK;
                        } else {
                            pddSummaryStatus = TimelineItemSummaryStatus.COMPLETELY_READY;
                        }
                    }
                    if (valuesAggregator.getCount() >= MIN_TESTS_COUNT && !testPercentageIsGood && lastTestSuccessful) {
                        pddSummaryStatus = TimelineItemSummaryStatus.TESTS_ARE_RED;
                    }
                    if (lastPddSectionLectureEvent == null) {
                        pddSummaryStatus = TimelineItemSummaryStatus.NO_LECTURE_YET;
                    }
                    timelineItemSummary.setTimelineItemSummaryStatus(pddSummaryStatus);
                    item.setTimelineItemSummary(timelineItemSummary);
                });
    }

    private static boolean isSectionTooLongWithoutTestsRepeating(final String sessionKey, final List<PddSectionTimelineItem> pddSectionTimelineItems, final LocalDate onDate) {
        TimelineItem lastPddSectionTesting = getLastPddSectionTestingEvent(sessionKey, pddSectionTimelineItems, TimeLineItemEventType.TESTING);
        if (lastPddSectionTesting == null) {
            return false;
        }
        PddSectionTesting lastSectionTesting = (PddSectionTesting) lastPddSectionTesting.getEvent();
        if (CommonUtils.getPercentage(lastSectionTesting.getTesting()) >= EXCELLENT_TEST_PERCENTAGE) {
            return CommonUtils.ageInDays(lastPddSectionTesting.getDate(), onDate) >= EXCELLENT_SECTION_TOO_LONG_WITHOUT_TESTING_DAYS;
        }
        if (CommonUtils.getPercentage(lastSectionTesting.getTesting()) >= COLL_TEST_PERCENTAGE) {
            return CommonUtils.ageInDays(lastPddSectionTesting.getDate(), onDate) >= COOL_SECTION_TOO_LONG_WITHOUT_TESTING_DAYS;
        }
        return CommonUtils.ageInDays(lastPddSectionTesting.getDate(), onDate) >= SECTION_TOO_LONG_WITHOUT_TESTING_DAYS;
    }

    private static boolean isSectionTooLongWithoutRestudy(final String sectionKey, final List<TimelineItem> schoolTimelineItems, final LocalDate onDate) {
        TimelineItem lastLectureStudyEvent = getLastLectureEvent(sectionKey, schoolTimelineItems, TimeLineItemEventType.LECTURE_STUDY);
        return lastLectureStudyEvent != null && CommonUtils.ageInDays(lastLectureStudyEvent.getDate(), onDate) > SECTION_TOO_LONG_WITHOUT_RESTUDY_DAYS;
    }

    private static TimelineItem getLastLectureEvent(final String pddSectionKey, final List<TimelineItem> schoolTimelineItems, final TimeLineItemEventType eventType) {
        TimelineItem schoolTimelineItem = schoolTimelineItems.stream()
                .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
                .filter(item -> item.getEvent().getEventType().equals(eventType))
                .filter(item -> {
                    AbstractLectureEvent event = (AbstractLectureEvent) item.getEvent();
                    return event.getPddSectionKey().equals(pddSectionKey);
                })
                .findFirst()
                .orElse(null);
        if (schoolTimelineItem == null) {
            return null;
        }
        return schoolTimelineItem;
    }

    private static TimelineItem getLastPddSectionTestingEvent(final String pddSectionKey, final List<PddSectionTimelineItem> pddSectionTimelineItems,
                                                              final TimeLineItemEventType eventType) {
        List<TimelineItem> requestedPddSectionTimelineItems = filterPddSectionTimelineItemsByPddSectionKey(pddSectionKey, pddSectionTimelineItems).stream()
                .filter(sectionItem -> eventType.equals(sectionItem.getEvent().getEventType()))
                .filter(tlItem -> tlItem.getEvent().getEventType().equals(eventType))
                .collect(Collectors.toList());
        if (requestedPddSectionTimelineItems.isEmpty()) {
            return null;
        }

        return requestedPddSectionTimelineItems.get(requestedPddSectionTimelineItems.size() - 1);
    }

    private static List<TimelineDayDto> convertTimelineDaysForPddSection(final PddSectionDto pddSection, final List<PddSectionTimelineItem> pddSectionTimelineItems,
                                                                         final List<TimelineDayColumn> dayColumns, final LocalDate onDate) {
        List<TimelineItem> requestedPddSectionTimelineItems = filterPddSectionTimelineItemsByPddSectionKey(pddSection.getKey(), pddSectionTimelineItems);
        return dayColumns.stream()
                .map(dayColumn -> {
                    TimelineDayDto timelineDay = new TimelineDayDto();
                    timelineDay.setDayIndex(dayColumn.getDayIndex());
                    timelineDay.setDayDate(dayColumn.getDate());

                    TimelineDayEventsDto dayEvents = new TimelineDayEventsDto();
                    populatePddSectionDayEvents(requestedPddSectionTimelineItems, dayColumn.getDate(), dayEvents);
                    dayEvents.setQuestionCount(pddSection.getQuestionsCount());
                    timelineDay.setDayEvents(dayEvents);
                    timelineDay.setWeekend(WEEKENDS.contains(dayColumn.getDate().getDayOfWeek()));
                    timelineDay.setToday(onDate.equals(dayColumn.getDate()));

                    return timelineDay;
                })
                .collect(Collectors.toList());
    }

    private static List<TimelineItem> filterPddSectionTimelineItemsByPddSectionKey(final String pddSectionKey, final List<PddSectionTimelineItem> pddSectionTimelineItems) {
        if (pddSectionTimelineItems == null) {
            return Collections.emptyList();
        }
        PddSectionTimelineItem pddSectionTimelineItem = pddSectionTimelineItems.stream()
                .filter(item -> item.getPddSection().getKey().equals(pddSectionKey))
                .findFirst()
                .orElse(null);
        if (pddSectionTimelineItem == null) {
            return Collections.emptyList();
        }
        return pddSectionTimelineItem.getTimelineItems();
    }

    private static void populateGlobalDayEvents(final List<TimelineItem> schoolTimelineItems, final TimelineDayColumn dayColumn) {
        schoolTimelineItems.stream()
                .filter(timelineItem -> timelineItem.getDate().equals(dayColumn.getDate()))
                .forEach(timelineItem -> {
                    TimelineEvent event = timelineItem.getEvent();
                    switch (event.getEventType()) {
                        case DRIVING:
                            AbstractDrivingEvent schoolDrivingEvent = (AbstractDrivingEvent) event;
                            CarDto carDto = TimelineObjectConverter.convertCar(schoolDrivingEvent.getCar());
                            InstructorDto instructor = TimelineObjectConverter.convertInstructor(schoolDrivingEvent.getInstructor());
                            boolean additionalDriving = event instanceof AdditionalDrivingEvent;
                            TimelineDayColumnEventsDto dayEvents = dayColumn.getColumnEvents();
                            if (additionalDriving) {
                                dayEvents.setDriving(new DrivingDto(carDto, instructor, schoolDrivingEvent.getDuration()));
                            } else {
                                dayEvents.setAdditionalDriving(new DrivingDto(carDto, instructor, schoolDrivingEvent.getDuration()));
                            }
                            break;
                        default:
                            break;
                    }
                });
    }

    private static void populatePddSectionDayEvents(final List<TimelineItem> pddSectionTimelineItems, final LocalDate onDate, final TimelineDayEventsDto dayEvents) {
        pddSectionTimelineItems.stream()
                .filter(timelineItem -> timelineItem.getDate().equals(onDate))
                .forEach(timelineItem -> {
                    TimelineEvent event = timelineItem.getEvent();
                    switch (event.getEventType()) {
                        case TESTING:
                            PddSectionTesting pddSectionTestingEvent = (PddSectionTesting) event;
                            dayEvents.setTesting(TimelineObjectConverter.convertTestingEvent(pddSectionTestingEvent.getTesting()));
                            break;
                        default:
                            throw new IllegalStateException(String.format("Not implemented yet: %s", event));
                    }
                });
    }

    @Getter
    private static class ValuesAggregator {
        private double value = 0D;
        private int count = 0;

        void add(final double value) {
            this.value += value;
            this.count++;
        }
    }
}
