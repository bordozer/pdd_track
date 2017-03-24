package com.pdd.track.converter;

import com.google.common.collect.Lists;
import com.pdd.track.dto.DrivingDto;
import com.pdd.track.dto.DrivingDto.CarDto;
import com.pdd.track.dto.DrivingDto.InstructorDto;
import com.pdd.track.dto.PddSectionDto;
import com.pdd.track.dto.TestingDto;
import com.pdd.track.dto.TimeLineDayHintDto;
import com.pdd.track.dto.TimelineDayColumn;
import com.pdd.track.dto.TimelineDayColumnEventsDto;
import com.pdd.track.dto.TimelineDayDto;
import com.pdd.track.dto.TimelineDayEventsDto;
import com.pdd.track.dto.TimelineDaySummaryDto;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.dto.TimelineDto.SectionDataHolder;
import com.pdd.track.dto.TimelineDto.TimelineStatistics;
import com.pdd.track.dto.TimelineItemDto;
import com.pdd.track.dto.TimelineItemSummaryDto;
import com.pdd.track.dto.TimelineItemSummaryDto.TimelineItemSummaryStatus;
import com.pdd.track.model.Timeline;
import com.pdd.track.model.Car;
import com.pdd.track.model.Instructor;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.PddSectionTimelineItem;
import com.pdd.track.model.StudyingTimelineItem;
import com.pdd.track.model.Testing;
import com.pdd.track.model.TimeLineDayHintType;
import com.pdd.track.model.TimeLineItemEventType;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.TimelineStudy;
import com.pdd.track.model.events.AbstractDrivingEvent;
import com.pdd.track.model.events.AdditionalDrivingEvent;
import com.pdd.track.model.events.PddSectionTesting;
import com.pdd.track.model.events.TimelineEvent;
import com.pdd.track.service.impl.DataGenerationServiceImpl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimelineConverter {

    private static final int SECTION_TOO_LONG_WITHOUT_REPEAT_DAYS = 5;
    private static final int COOL_SECTION_TOO_LONG_WITHOUT_REPEAT_DAYS = 7;
    private static final int EXCELLENT_SECTION_TOO_LONG_WITHOUT_REPEAT_DAYS = 10;
    private static final int SECTION_TOO_LONG_WITHOUT_STUDY_DAYS = 5;
    private static final int MIN_TESTS_COUNT = 3;
    private static final EnumSet<DayOfWeek> WEEKENDS = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    public static final int GOOD_TEST_PERCENTAGE = 90;
    public static final int COLL_TEST_PERCENTAGE = 96;
    public static final int EXCELLENT_TEST_PERCENTAGE = 100;

    public static TimelineDto toDto(final Timeline timeline, final TimelineStudy timelineStudy, final LocalDate onDate) {
        TimelineDto result = new TimelineDto();
        result.setStartDate(DataGenerationServiceImpl.STUDY_START_DAY);
        result.setEndDate(DataGenerationServiceImpl.STUDY_END_DAY);

        List<TimelineDayColumn> dayColumns = getDayColumns(onDate);
        result.setDayColumns(dayColumns);
        dayColumns.stream()
                .forEach(dayColumn -> {
                    populateGlobalDayEvents(dayColumn, timeline.getStudyingTimelineItems());
                });

        List<PddSectionTimelineItem> timelineStudyItems = timelineStudy.getPddSectionTimelineItems();
        result.setItems(convertItems(timelineStudyItems, dayColumns, onDate));
        result.setSummaryColumns(calculateTimelineDaySummary(dayColumns, timelineStudyItems));
        result.setTimelineStatistics(collectStatistics(result));

        return result;
    }

    private static TimelineStatistics collectStatistics(final TimelineDto result) {
        TimelineStatistics statistics = new TimelineStatistics();

        List<TimelineItemDto> totalSections = result.getItems().stream().collect(Collectors.toList());
        int totalSectionsCount = totalSections.size();
        int totalQuestionsCount = totalSections.stream().mapToInt(item -> item.getPddSection().getQuestionsCount()).sum();
        SectionDataHolder sectionDataHolder = new SectionDataHolder("Section total", totalSectionsCount, formatDouble(100), totalQuestionsCount, formatDouble(100));
        statistics.add(sectionDataHolder);

        List<TimelineItemDto> lectureSections = result.getItems().stream().filter(item -> item.getTimelineItemSummary().isLecture()).collect(Collectors.toList());
        int lectureSectionsCount = lectureSections.size();
        int lectureQuestionsCount = lectureSections.stream().mapToInt(item -> item.getPddSection().getQuestionsCount()).sum();
        String lectureSectionsPercentage = formatDouble((double) lectureSectionsCount / totalSectionsCount * 100);
        String lectureQuestionsPercentage = formatDouble((double) lectureQuestionsCount / totalQuestionsCount * 100);
        statistics.add(new SectionDataHolder("Lectured", lectureSectionsCount, lectureSectionsPercentage, lectureQuestionsCount, lectureQuestionsPercentage));

        List<TimelineItemDto> studySections = result.getItems().stream().filter(item -> item.getTimelineItemSummary().isStudy()).collect(Collectors.toList());
        int studySectionsCount = studySections.size();
        int studyQuestionsCount = studySections.stream().mapToInt(item -> item.getPddSection().getQuestionsCount()).sum();
        String studySectionsPercentage = formatDouble((double) studySectionsCount / totalSectionsCount * 100);
        String studyQuestionsPercentage = formatDouble((double) studyQuestionsCount / totalQuestionsCount * 100);
        statistics.add(new SectionDataHolder("Study", studySectionsCount, studySectionsPercentage, studyQuestionsCount, studyQuestionsPercentage));

        statistics.add(getSectionDataHolder(result, "Not studied or red tests", totalSectionsCount, totalQuestionsCount, TimelineItemSummaryStatus.NOT_READY, TimelineItemSummaryStatus.TO_STUDY));
        statistics.add(getSectionDataHolder(result, "Ready with risks", totalSectionsCount, totalQuestionsCount, TimelineItemSummaryStatus.READY_WITH_RISK));
        statistics.add(getSectionDataHolder(result, "Completely ready", totalSectionsCount, totalQuestionsCount, TimelineItemSummaryStatus.COMPLETELY_READY));
        statistics.add(getSectionDataHolder(result, "Ready or ready with risks", totalSectionsCount, totalQuestionsCount, TimelineItemSummaryStatus.COMPLETELY_READY, TimelineItemSummaryStatus.READY_WITH_RISK));

        return statistics;
    }

    private static SectionDataHolder getSectionDataHolder(final TimelineDto result, final String title, final int totalSectionsCount, final int totalQuestionsCount, final TimelineItemSummaryStatus... summaryStatuses) {
        List<TimelineItemDto> sections = result.getItems().stream().filter(item -> Lists.newArrayList(summaryStatuses).contains(item.getTimelineItemSummary().getTimelineItemSummaryStatus())).collect(Collectors.toList());
        int sectionsCount = sections.size();
        int questionsCount = sections.stream().mapToInt(item -> item.getPddSection().getQuestionsCount()).sum();
        String sectionsPercentage = formatDouble((double) sectionsCount / totalSectionsCount * 100);
        String questionsPercentage = formatDouble((double) questionsCount / totalQuestionsCount * 100);
        return new SectionDataHolder(title, sectionsCount, sectionsPercentage, questionsCount, questionsPercentage);
    }

    private static List<TimelineDaySummaryDto> calculateTimelineDaySummary(final List<TimelineDayColumn> dayColumns, final List<PddSectionTimelineItem> pddSectionTimelineItems) {

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
                                            questionsAggregator.add(item.getPddSection().getQuestionsCount());
                                        });
                            });

                    if (valuesAggregator.getValue() == 0) {
                        return new TimelineDaySummaryDto(0, "", 0);
                    }
                    double value = valuesAggregator.getValue() / valuesAggregator.getCount();
                    return new TimelineDaySummaryDto(value, formatDouble(value), questionsAggregator.getValue());
                })
                .collect(Collectors.toList());
    }

    private static ValuesAggregator calculateTimelinePddSectionSummary(final List<PddSectionTimelineItem> timelineStudyItems, final String pddSectionKey) {
        final ValuesAggregator valuesAggregator = new ValuesAggregator();
        timelineStudyItems.stream()
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

    private static List<TimelineItemDto> convertItems(final List<PddSectionTimelineItem> timelineStudyItems, final List<TimelineDayColumn> dayColumns, final LocalDate onDate) {
        List<TimelineItemDto> result = groupPddSections(timelineStudyItems).stream()
                .map(sectionDto -> {
                    TimelineItemDto item = new TimelineItemDto();
                    item.setPddSection(sectionDto);
                    item.setTimelineDays(convertTimelineDaysForPddSection(sectionDto, timelineStudyItems, dayColumns, onDate));
                    return item;
                })
                .collect(Collectors.toList());
        populateHintsOnDate(timelineStudyItems, result, onDate);
        LocalDate aDate = onDate.plusDays(1);
        while(aDate.isBefore(DataGenerationServiceImpl.STUDY_END_DAY.plusDays(1))) {
            populateHintsOnDate(timelineStudyItems, result, aDate);
            aDate = aDate.plusDays(1);
        }
        populateTimelineSummary(timelineStudyItems, result, onDate);
        return result;
    }

    private static void populateHintsOnDate(final List<PddSectionTimelineItem> timelineStudyItems, final List<TimelineItemDto> result, final LocalDate onDate) {
        result.stream()
                .forEach(item -> {
                    item.getTimelineDays().stream()
                            .forEach(day -> {
                                if (!day.getDayDate().equals(onDate)) {
                                    return;
                                }
                                String sectionKey = item.getPddSection().getKey();
                                int sessionQuestionCount = item.getPddSection().getQuestionsCount();
                                TimelineItem pddSectionStudyEvent = getLastPddSectionEvent(sectionKey, timelineStudyItems, TimeLineItemEventType.STUDY);
                                if (pddSectionStudyEvent == null) {
                                    TimelineItem pddSectionLectureEvent = getLastPddSectionEvent(sectionKey, timelineStudyItems, TimeLineItemEventType.LECTURE);
                                    if (pddSectionLectureEvent != null && ageInDays(pddSectionLectureEvent.getDate(), onDate) > SECTION_TOO_LONG_WITHOUT_STUDY_DAYS) {
                                        day.setDayHints(Lists.newArrayList(new TimeLineDayHintDto(TimeLineDayHintType.NEEDS_STUDY, ageInDays(pddSectionLectureEvent.getDate(), onDate), sessionQuestionCount)));
                                    }
                                    return;
                                }
                                TimelineItem lastTesting = getLastPddSectionEvent(sectionKey, timelineStudyItems, TimeLineItemEventType.TESTING);
                                if (lastTesting == null) {
                                    return;
                                }
                                if (isSectionTooLongWithoutRepeating(timelineStudyItems, sectionKey, onDate)) {
                                    day.setDayHints(Lists.newArrayList(new TimeLineDayHintDto(TimeLineDayHintType.ADVICE_REFRESH_TESTS, ageInDays(lastTesting.getDate(), onDate), sessionQuestionCount)));
                                }
                                PddSectionTesting pddSectionTestingEvent = (PddSectionTesting) lastTesting.getEvent();
                                if (!pddSectionTestingEvent.getTesting().isPassed()) {
                                    day.setDayHints(Lists.newArrayList(new TimeLineDayHintDto(TimeLineDayHintType.RED_TESTS, ageInDays(lastTesting.getDate(), onDate), sessionQuestionCount)));
                                }
                            });
                });
    }

    private static boolean isSectionTooLongWithoutRepeating(final List<PddSectionTimelineItem> timelineStudyItems, final String sessionKey, final LocalDate onDate) {
        TimelineItem lastPddSectionTesting = getLastPddSectionEvent(sessionKey, timelineStudyItems, TimeLineItemEventType.TESTING);
        if (lastPddSectionTesting == null) {
            return false;
        }
        PddSectionTesting lastSectionTesting = (PddSectionTesting) lastPddSectionTesting.getEvent();
        if (getPercentage(lastSectionTesting.getTesting()) >= EXCELLENT_TEST_PERCENTAGE) {
            return ageInDays(lastPddSectionTesting.getDate(), onDate) > EXCELLENT_SECTION_TOO_LONG_WITHOUT_REPEAT_DAYS;
        }
        if (getPercentage(lastSectionTesting.getTesting()) >= COLL_TEST_PERCENTAGE) {
            return ageInDays(lastPddSectionTesting.getDate(), onDate) > COOL_SECTION_TOO_LONG_WITHOUT_REPEAT_DAYS;
        }
        return ageInDays(lastPddSectionTesting.getDate(), onDate) > SECTION_TOO_LONG_WITHOUT_REPEAT_DAYS;
    }

    private static void populateTimelineSummary(final List<PddSectionTimelineItem> timelineStudyItems, final List<TimelineItemDto> result, final LocalDate onDate) {
        result.stream()
                .forEach(item -> {
                    String sectionKey = item.getPddSection().getKey();
                    TimelineItem pddSectionLectureEvent = getLastPddSectionEvent(sectionKey, timelineStudyItems, TimeLineItemEventType.LECTURE);
                    TimelineItem pddSectionStudyEvent = getLastPddSectionEvent(sectionKey, timelineStudyItems, TimeLineItemEventType.STUDY);

                    TimelineItemSummaryDto timelineItemSummary = new TimelineItemSummaryDto();
                    timelineItemSummary.setLecture(pddSectionLectureEvent != null);
                    timelineItemSummary.setStudy(pddSectionStudyEvent != null);


                    boolean lastTestSuccessful = false;
                    TimelineItem lastPddSectionTesting = getLastPddSectionEvent(sectionKey, timelineStudyItems, TimeLineItemEventType.TESTING);
                    if (lastPddSectionTesting != null) {
                        PddSectionTesting lastSectionTesting = (PddSectionTesting) lastPddSectionTesting.getEvent();
                        lastTestSuccessful = lastSectionTesting.getTesting().isPassed();
                        timelineItemSummary.setLastTestSuccessful(lastTestSuccessful);
                    }

                    ValuesAggregator valuesAggregator = calculateTimelinePddSectionSummary(timelineStudyItems, sectionKey);
                    double averageTestingScore = valuesAggregator.getValue() / valuesAggregator.getCount();
                    timelineItemSummary.setTestsCount(valuesAggregator.getCount());
                    timelineItemSummary.setTestsAveragePercentage(averageTestingScore);
                    timelineItemSummary.setTestsAveragePercentageFormatted(formatDouble(averageTestingScore));
                    boolean testPercentageIsGood = averageTestingScore > GOOD_TEST_PERCENTAGE;

                    TimelineItemSummaryStatus pddSummaryStatus = TimelineItemSummaryStatus.NONE;
                    boolean itWasLectureButItIsNotStudied = pddSectionLectureEvent != null && pddSectionStudyEvent == null;
                    if (itWasLectureButItIsNotStudied) {
                        pddSummaryStatus = TimelineItemSummaryStatus.TO_STUDY;
                    }
                    if (!itWasLectureButItIsNotStudied && valuesAggregator.getCount() > 0 && valuesAggregator.getCount() < MIN_TESTS_COUNT) {
                        pddSummaryStatus = TimelineItemSummaryStatus.NOT_READY;
                    }
                    if (valuesAggregator.getCount() >= MIN_TESTS_COUNT && testPercentageIsGood && lastTestSuccessful) {
                        if (isSectionTooLongWithoutRepeating(timelineStudyItems, sectionKey, onDate)) {
                            pddSummaryStatus = TimelineItemSummaryStatus.READY_WITH_RISK;
                        } else {
                            pddSummaryStatus = TimelineItemSummaryStatus.COMPLETELY_READY;
                        }
                    }
                    if (valuesAggregator.getCount() >= MIN_TESTS_COUNT && !testPercentageIsGood && lastTestSuccessful) {
                        pddSummaryStatus = TimelineItemSummaryStatus.NOT_READY;
                    }
                    if (pddSectionLectureEvent == null) {
                        pddSummaryStatus = TimelineItemSummaryStatus.NO_LECTURE_YET;
                    }
                    timelineItemSummary.setTimelineItemSummaryStatus(pddSummaryStatus);
                    item.setTimelineItemSummary(timelineItemSummary);
                });
    }

    private static TimelineItem getLastPddSectionEvent(final String pddSectionKey, final List<PddSectionTimelineItem> timelineStudyItems, final TimeLineItemEventType eventType) {
        List<PddSectionTimelineItem> pddSectionTimelineItems = timelineStudyItems.stream()
                .filter(sectionItem -> {
                    if (!sectionItem.getPddSection().getKey().equals(pddSectionKey)) {
                        return false;
                    }
                    List<TimelineItem> collect = sectionItem.getTimelineItems().stream()
                            .filter(tlItem -> eventType.equals(tlItem.getEvent().getEventType()))
                            .collect(Collectors.toList());
                    return !collect.isEmpty();
                })
                .collect(Collectors.toList());
        List<TimelineItem> timelineTestingItems = pddSectionTimelineItems.stream()
                .map(PddSectionTimelineItem::getTimelineItems)
                .flatMap(List::stream)
                .filter(tlItem -> tlItem.getEvent().getEventType().equals(eventType))
                .collect(Collectors.toList());
        if (timelineTestingItems.isEmpty()) {
            return null;
        }

        return timelineTestingItems.get(timelineTestingItems.size() - 1);
    }

    private static List<TimelineDayDto> convertTimelineDaysForPddSection(final PddSectionDto pddSection, final List<PddSectionTimelineItem> timelineStudyItems, final List<TimelineDayColumn> dayColumns, final LocalDate onDate) {
        List<TimelineItem> pddSectionTimelineItems = filterTimelineItemsByPddSectionKey(pddSection.getKey(), timelineStudyItems);
        return dayColumns.stream()
                .map(dayColumn -> {
                    TimelineDayDto timelineDay = new TimelineDayDto();
                    timelineDay.setDayIndex(dayColumn.getDayIndex());
                    timelineDay.setDayDate(dayColumn.getDate());

                    TimelineDayEventsDto dayEvents = new TimelineDayEventsDto();
                    populatePddSectionDayEvents(dayColumn.getDate(), pddSectionTimelineItems, dayEvents);
                    dayEvents.setQuestionCount(pddSection.getQuestionsCount());
                    timelineDay.setDayEvents(dayEvents);
                    timelineDay.setWeekend(WEEKENDS.contains(dayColumn.getDate().getDayOfWeek()));
                    timelineDay.setToday(onDate.equals(dayColumn.getDate()));

                    return timelineDay;
                })
                .collect(Collectors.toList());
    }

    private static List<TimelineItem> filterTimelineItemsByPddSectionKey(final String pddSectionKey, final List<PddSectionTimelineItem> pddSectionTimelineItems) {
        return pddSectionTimelineItems.stream()
                .filter(section -> section.getPddSection().getKey().equals(pddSectionKey))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .getTimelineItems();
    }

    private static void populateGlobalDayEvents(final TimelineDayColumn dayColumn, final List<StudyingTimelineItem> studyingTimelineItems) {
        studyingTimelineItems.stream()
                .filter(timelineItem -> timelineItem.getTimelineItem().getDate().equals(dayColumn.getDate()))
                .forEach(timelineItem -> {
                    TimelineEvent event = timelineItem.getTimelineItem().getEvent();
                    switch (event.getEventType()) {
                        case DRIVING:
                            AbstractDrivingEvent schoolDrivingEvent = (AbstractDrivingEvent) event;
                            CarDto carDto = convertCar(schoolDrivingEvent.getCar());
                            InstructorDto instructor = convertInstructor(schoolDrivingEvent.getInstructor());
                            boolean additionalDriving = event instanceof AdditionalDrivingEvent;
                            TimelineDayColumnEventsDto dayEvents = dayColumn.getColumnEvents();
                            if (additionalDriving) {
                                dayEvents.setDriving(new DrivingDto(carDto, instructor, schoolDrivingEvent.getDuration()));
                            } else {
                                dayEvents.setAdditionalDriving(new DrivingDto(carDto, instructor, schoolDrivingEvent.getDuration()));
                            }
                            break;
                        default:
                            throw new IllegalStateException(String.format("Not implemented yet: %s", event));
                    }
                });
    }

    private static void populatePddSectionDayEvents(final LocalDate timelineDate, final List<TimelineItem> pddSectionTimelineItems, final TimelineDayEventsDto dayEvents) {
        pddSectionTimelineItems.stream()
                .filter(timelineItem -> timelineItem.getDate().equals(timelineDate))
                .forEach(timelineItem -> {
                    TimelineEvent event = timelineItem.getEvent();
                    switch (event.getEventType()) {
                        case LECTURE:
                            dayEvents.setLecture(true);
                            break;
                        case STUDY:
                            dayEvents.setStudy(true);
                            break;
                        case TESTING:
                            PddSectionTesting pddSectionTestingEvent = (PddSectionTesting) event;
                            dayEvents.setTesting(convertTestingEvent(pddSectionTestingEvent.getTesting()));
                            break;
                        default:
                            throw new IllegalStateException(String.format("Not implemented yet: %s", event));
                    }
                });
    }

    private static TestingDto convertTestingEvent(final Testing testing) {
        double percentage = getPercentage(testing);
        String percentageFormatted = formatDouble(percentage);
        return new TestingDto(testing.getPassedQuestions(), testing.getTotalQuestions(), testing.isPassed(), percentage, percentageFormatted);
    }

    private static double getPercentage(final Testing testing) {
        return (double) testing.getPassedQuestions() / testing.getTotalQuestions() * 100;
    }

    private static CarDto convertCar(final Car car) {
        return new CarDto(car.getModel());
    }

    private static InstructorDto convertInstructor(final Instructor instructor) {
        return new InstructorDto(instructor.getName());
    }

    private static List<PddSectionDto> groupPddSections(final List<PddSectionTimelineItem> pddSectionTimelineItems) {
        return pddSectionTimelineItems.stream()
                .map(section -> {
                    PddSection pddSection = section.getPddSection();
                    PddSectionDto dto = new PddSectionDto();
                    dto.setKey(pddSection.getKey());
                    dto.setNumber(pddSection.getNumber());
                    dto.setName(pddSection.getName());
                    dto.setQuestionsCount(pddSection.getQuestionsCount());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private static String formatDouble(final double value) {
        return value == 0 ? "" : new DecimalFormat("#0.00").format(value);
    }

    private static long ageInDays(final LocalDate date, final LocalDate onDate) {
        return ChronoUnit.DAYS.between(date, onDate);
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
