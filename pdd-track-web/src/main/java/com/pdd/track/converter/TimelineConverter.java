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
import com.pdd.track.model.Car;
import com.pdd.track.model.Instructor;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.PddSectionTimelineItem;
import com.pdd.track.model.SchoolTimeline;
import com.pdd.track.model.StudentTimeline;
import com.pdd.track.model.Testing;
import com.pdd.track.model.TimeLineDayHintType;
import com.pdd.track.model.TimeLineItemEventType;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.events.AbstractDrivingEvent;
import com.pdd.track.model.events.AdditionalDrivingEvent;
import com.pdd.track.model.events.LectureEvent;
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

    public static TimelineDto toDto(final List<PddSection> pddSections, final SchoolTimeline schoolSchoolTimeline, final StudentTimeline studentTimeline, final LocalDate onDate) {
        TimelineDto result = new TimelineDto();
        result.setStartDate(DataGenerationServiceImpl.STUDY_START_DAY);
        result.setEndDate(DataGenerationServiceImpl.STUDY_END_DAY);

        List<TimelineItem> schoolTimelineItems = schoolSchoolTimeline.getTimelineItems();
        List<PddSectionTimelineItem> studentTimelineItems = studentTimeline.getPddSectionTimelineItems();

        List<TimelineDayColumn> dayColumns = getDayColumns(onDate);
        result.setDayColumns(dayColumns);
        dayColumns.stream()
                .forEach(dayColumn -> {
                    populateGlobalDayEvents(dayColumn, schoolTimelineItems);
                });

        result.setItems(convertTimelineItems(pddSections, schoolTimelineItems, studentTimelineItems, dayColumns, onDate));
        result.setSummaryColumns(calculateTimelineDaySummary(dayColumns, studentTimelineItems));
        result.setTimelineStatistics(collectStatistics(result));

        return result;
    }

    private static List<TimelineItemDto> convertTimelineItems(final List<PddSection> pddSections, final List<TimelineItem> schoolTimelineItems, final List<PddSectionTimelineItem> studentTimelineItems, final List<TimelineDayColumn> dayColumns, final LocalDate onDate) {
        List<TimelineItemDto> result = pddSections.stream()
                .map(section -> {
                    PddSectionDto sectionDto = convertOddSection(section);
                    TimelineItemDto item1 = new TimelineItemDto();
                    item1.setPddSection(sectionDto);
                    item1.setTimelineDays(convertTimelineDaysForPddSection(sectionDto, studentTimelineItems, dayColumns, onDate));
                    return item1;
                })
                .collect(Collectors.toList());
        populateLectureEvents(schoolTimelineItems, result);
        populateHintsOnDate(schoolTimelineItems, studentTimelineItems, result, onDate);
        populateFutureHints(schoolTimelineItems, studentTimelineItems, onDate, result);
        populateTimelineSummary(schoolTimelineItems, studentTimelineItems, result, onDate);
        return result;
    }

    private static void populateLectureEvents(final List<TimelineItem> schoolTimelineItems, final List<TimelineItemDto> studentTimelineItems) {
        studentTimelineItems.stream()
                .forEach(tlsItem -> {
                    tlsItem.getTimelineDays().stream()
                            .forEach(tlDay -> {
                                String pddSectionKey = tlsItem.getPddSection().getKey();
                                TimelineItem lecture = schoolTimelineItems.stream()
                                        .filter(stlItem -> {
                                            if (!stlItem.getEvent().getEventType().equals(TimeLineItemEventType.LECTURE)) {
                                                return false;
                                            }
                                            if (!stlItem.getDate().equals(tlDay.getDayDate())) {
                                                return false;
                                            }
                                            LectureEvent timelineLectureEvent = (LectureEvent) stlItem.getEvent();
                                            return timelineLectureEvent.getPddSectionKey().equals(pddSectionKey);
                                        })
                                        .findFirst()
                                        .orElse(null);
                                tlDay.getDayEvents().setLecture(lecture != null);
                            });
                });
    }

    private static void populateFutureHints(final List<TimelineItem> schoolTimelineItems, final List<PddSectionTimelineItem> timelineStudyItems, final LocalDate onDate, final List<TimelineItemDto> result) {
        LocalDate aDate = onDate.plusDays(1);
        while (aDate.isBefore(DataGenerationServiceImpl.STUDY_END_DAY.plusDays(1))) {
            populateHintsOnDate(schoolTimelineItems, timelineStudyItems, result, aDate);
            aDate = aDate.plusDays(1);
        }
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

    private static void populateHintsOnDate(final List<TimelineItem> schoolTimelineItems, final List<PddSectionTimelineItem> studentTimelineItems, final List<TimelineItemDto> result, final LocalDate onDate) {
        result.stream()
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
                                TimelineItem pddSectionStudyEvent = getLastStudyEvent(sectionKey, schoolTimelineItems, TimeLineItemEventType.STUDY);
                                if (pddSectionStudyEvent == null) {
                                    TimelineItem pddSectionLectureEvent = getLastStudyEvent(sectionKey, schoolTimelineItems, TimeLineItemEventType.LECTURE);
                                    if (pddSectionLectureEvent != null && ageInDays(pddSectionLectureEvent.getDate(), onDate) > SECTION_TOO_LONG_WITHOUT_STUDY_DAYS) {
                                        dayHints.add(new TimeLineDayHintDto(TimeLineDayHintType.NEEDS_STUDY, ageInDays(pddSectionLectureEvent.getDate(), onDate), sessionQuestionCount));
                                    }
//                                    return;
                                } else {
                                    LocalDate lastRestudyDate = pddSectionStudyEvent.getDate();
                                    if (ageInDays(lastRestudyDate, onDate) > SECTION_TOO_LONG_WITHOUT_RESTUDY_DAYS) {
                                        dayHints.add(new TimeLineDayHintDto(TimeLineDayHintType.NEEDS_RESTUDY, ageInDays(lastRestudyDate, onDate), sessionQuestionCount));
                                    }
                                }
                                TimelineItem lastTesting = getLastPddSectionTestingEvent(sectionKey, studentTimelineItems, TimeLineItemEventType.TESTING);
                                if (lastTesting == null) {
                                    return;
                                }
                                if (isSectionTooLongWithoutRepeating(studentTimelineItems, sectionKey, onDate)) {
                                    dayHints.add(new TimeLineDayHintDto(TimeLineDayHintType.ADVICE_REFRESH_TESTS, ageInDays(lastTesting.getDate(), onDate), sessionQuestionCount));
                                }
                                PddSectionTesting pddSectionTestingEvent = (PddSectionTesting) lastTesting.getEvent();
                                if (!pddSectionTestingEvent.getTesting().isPassed()) {
                                    dayHints.add(new TimeLineDayHintDto(TimeLineDayHintType.RED_TESTS, ageInDays(lastTesting.getDate(), onDate), sessionQuestionCount));
                                }
                            });
                });
    }

    private static boolean isSectionTooLongWithoutRepeating(final List<PddSectionTimelineItem> timelineStudyItems, final String sessionKey, final LocalDate onDate) {
        TimelineItem lastPddSectionTesting = getLastPddSectionTestingEvent(sessionKey, timelineStudyItems, TimeLineItemEventType.TESTING);
        if (lastPddSectionTesting == null) {
            return false;
        }
        PddSectionTesting lastSectionTesting = (PddSectionTesting) lastPddSectionTesting.getEvent();
        if (getPercentage(lastSectionTesting.getTesting()) >= EXCELLENT_TEST_PERCENTAGE) {
            return ageInDays(lastPddSectionTesting.getDate(), onDate) > EXCELLENT_SECTION_TOO_LONG_WITHOUT_TESTING_DAYS;
        }
        if (getPercentage(lastSectionTesting.getTesting()) >= COLL_TEST_PERCENTAGE) {
            return ageInDays(lastPddSectionTesting.getDate(), onDate) > COOL_SECTION_TOO_LONG_WITHOUT_TESTING_DAYS;
        }
        return ageInDays(lastPddSectionTesting.getDate(), onDate) > SECTION_TOO_LONG_WITHOUT_TESTING_DAYS;
    }

    private static void populateTimelineSummary(final List<TimelineItem> studyItems, final List<PddSectionTimelineItem> timelineStudyItems, final List<TimelineItemDto> result, final LocalDate onDate) {
        result.stream()
                .forEach(item -> {
                    String sectionKey = item.getPddSection().getKey();
                    TimelineItem pddSectionLectureEvent = getLastStudyEvent(sectionKey, studyItems, TimeLineItemEventType.LECTURE);
                    TimelineItem pddSectionStudyEvent = getLastStudyEvent(sectionKey, studyItems, TimeLineItemEventType.STUDY);

                    TimelineItemSummaryDto timelineItemSummary = new TimelineItemSummaryDto();
                    timelineItemSummary.setLecture(pddSectionLectureEvent != null);
                    timelineItemSummary.setStudy(pddSectionStudyEvent != null);


                    boolean lastTestSuccessful = false;
                    TimelineItem lastPddSectionTesting = getLastPddSectionTestingEvent(sectionKey, timelineStudyItems, TimeLineItemEventType.TESTING);
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

    private static TimelineItem getLastStudyEvent(final String pddSectionKey, final List<TimelineItem> schoolTimelineItems, final TimeLineItemEventType eventType) {
        TimelineItem schoolTimelineItem = schoolTimelineItems.stream()
                .sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate()))
                .filter(item -> item.getEvent().getEventType().equals(TimeLineItemEventType.LECTURE))
                .filter(item -> {
                    LectureEvent event = (LectureEvent) item.getEvent();
                    return event.getPddSectionKey().equals(pddSectionKey);
                })
                .findFirst()
                .orElse(null);
        if (schoolTimelineItem == null) {
            return null;
        }
        return schoolTimelineItem;
    }

    private static TimelineItem getLastPddSectionTestingEvent(final String pddSectionKey, final List<PddSectionTimelineItem> timelineStudyItems, final TimeLineItemEventType eventType) {
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
        PddSectionTimelineItem pddSectionTimelineItem = pddSectionTimelineItems.stream()
                .filter(item -> item.getPddSection().getKey().equals(pddSectionKey))
                .findFirst()
                .orElse(null);
        if (pddSectionTimelineItem == null) {
            return Collections.emptyList();
        }
        return pddSectionTimelineItem.getTimelineItems();
    }

    private static void populateGlobalDayEvents(final TimelineDayColumn dayColumn, final List<TimelineItem> schoolTimelineItems) {
        schoolTimelineItems.stream()
                .filter(timelineItem -> timelineItem.getDate().equals(dayColumn.getDate()))
                .forEach(timelineItem -> {
                    TimelineEvent event = timelineItem.getEvent();
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
                            break;
                    }
                });
    }

    private static void populatePddSectionDayEvents(final LocalDate timelineDate, final List<TimelineItem> pddSectionTimelineItems, final TimelineDayEventsDto dayEvents) {
        pddSectionTimelineItems.stream()
                .filter(timelineItem -> timelineItem.getDate().equals(timelineDate))
                .forEach(timelineItem -> {
                    TimelineEvent event = timelineItem.getEvent();
                    switch (event.getEventType()) {
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
                .map(item -> TimelineConverter.convertOddSection(item.getPddSection()))
                .collect(Collectors.toList());
    }

    private static PddSectionDto convertOddSection(final PddSection pddSection) {
        PddSectionDto dto = new PddSectionDto();
        dto.setKey(pddSection.getKey());
        dto.setNumber(pddSection.getNumber());
        dto.setName(pddSection.getName());
        dto.setQuestionsCount(pddSection.getQuestionsCount());
        return dto;
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
