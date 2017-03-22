package com.pdd.track.converter;

import com.pdd.track.dto.DrivingDto;
import com.pdd.track.dto.DrivingDto.CarDto;
import com.pdd.track.dto.DrivingDto.InstructorDto;
import com.pdd.track.dto.PddSectionDto;
import com.pdd.track.dto.TestingDto;
import com.pdd.track.dto.TimelineDayColumn;
import com.pdd.track.dto.TimelineDayColumnEventsDto;
import com.pdd.track.dto.TimelineDayDto;
import com.pdd.track.dto.TimelineDayEventsDto;
import com.pdd.track.dto.TimelineDaySummaryDto;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.dto.TimelineItemDto;
import com.pdd.track.dto.TimelineItemSummaryDto;
import com.pdd.track.entity.TimelineEntity;
import com.pdd.track.model.Car;
import com.pdd.track.model.Instructor;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.PddSectionTimelineItem;
import com.pdd.track.model.StudyingTimelineItem;
import com.pdd.track.model.Testing;
import com.pdd.track.model.TimeLineItemEventType;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.events.AbstractDrivingEvent;
import com.pdd.track.model.events.AdditionalDrivingEvent;
import com.pdd.track.model.events.PddSectionTesting;
import com.pdd.track.model.events.TimelineEvent;
import com.pdd.track.service.impl.DataGenerationServiceImpl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimelineConverter {

    public static TimelineDto toDto(final TimelineEntity entity) {
        TimelineDto result = new TimelineDto();
        result.setStartDate(DataGenerationServiceImpl.STUDY_START_DAY);
        result.setEndDate(DataGenerationServiceImpl.STUDY_END_DAY);

        List<TimelineDayColumn> dayColumns = getDayColumns();
        result.setDayColumns(dayColumns);
        dayColumns.stream()
                .forEach(dayColumn -> {
                    populateGlobalDayEvents(dayColumn, entity.getStudyingTimelineItems());
                });

        result.setItems(convertItems(entity, dayColumns));
        result.setSummaryColumns(calculateTimelineDaySummary(entity, dayColumns));

        return result;
    }

    private static List<TimelineDaySummaryDto> calculateTimelineDaySummary(final TimelineEntity entity, final List<TimelineDayColumn> dayColumns) {
        return dayColumns.stream()
                .map(dayColumn -> {
                    final VHolder vHolder = new VHolder();
                    entity.getPddSectionTimelineItems().stream()
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
                                            vHolder.add(((double) testingEvent.getTesting().getPassedQuestions() / testingEvent.getTesting().getTotalQuestions()) * 100);
                                        });
                            });

                    if (vHolder.getValue() == 0) {
                        return new TimelineDaySummaryDto(0, "");
                    }
                    double value = vHolder.getValue() / vHolder.getCount();
                    return new TimelineDaySummaryDto(value, formatDouble(value));
                })
                .collect(Collectors.toList());
    }

    private static VHolder calculateTimelinePddSectionSummary(final TimelineEntity entity, final String pddSectionKey) {
        final VHolder vHolder = new VHolder();
        entity.getPddSectionTimelineItems().stream()
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
                                vHolder.add(((double) testingEvent.getTesting().getPassedQuestions() / testingEvent.getTesting().getTotalQuestions()) * 100);
                            });
                });
        return vHolder;
    }

    private static List<TimelineDayColumn> getDayColumns() {
        int index = 1;
        LocalDate today = LocalDate.now();
        LocalDate currentDay = DataGenerationServiceImpl.STUDY_START_DAY;
        List<TimelineDayColumn> result = new ArrayList<>();
        while (currentDay.isBefore(DataGenerationServiceImpl.STUDY_END_DAY.plusDays(1))) {
            TimelineDayColumn dayColumn = new TimelineDayColumn();
            dayColumn.setDayIndex(index);
            dayColumn.setDate(currentDay);

            TimelineDayColumnEventsDto columnEvents = new TimelineDayColumnEventsDto();
            if (currentDay.isBefore(today)) {
                columnEvents.setDayPassed(true);
            }
            if (currentDay.equals(today)) {
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

    private static List<TimelineItemDto> convertItems(final TimelineEntity entity, final List<TimelineDayColumn> dayColumns) {
        List<TimelineItemDto> result = groupPddSections(entity.getPddSectionTimelineItems()).stream()
                .map(sectionDto -> {
                    TimelineItemDto item = new TimelineItemDto();
                    item.setPddSection(sectionDto);
                    item.setTimelineDays(convertTimelineDaysForPddSection(sectionDto.getKey(), entity, dayColumns));
                    return item;
                })
                .collect(Collectors.toList());
        result.stream()
                .forEach(tlItem -> {
                    TimelineItem pddSectionLectureEvent = getLastPddSectionEvent(tlItem.getPddSection().getKey(), TimeLineItemEventType.LECTURE, entity);
                    TimelineItem pddSectionStudyEvent = getLastPddSectionEvent(tlItem.getPddSection().getKey(), TimeLineItemEventType.STUDY, entity);
                    TimelineItem lastPddSectionTesting = getLastPddSectionEvent(tlItem.getPddSection().getKey(), TimeLineItemEventType.TESTING, entity);
                    PddSectionTesting pddSectionTesting = (PddSectionTesting) lastPddSectionTesting.getEvent();

                    TimelineItemSummaryDto timelineItemSummary = new TimelineItemSummaryDto();
                    timelineItemSummary.setLecture(pddSectionLectureEvent != null);
                    timelineItemSummary.setStudy(pddSectionStudyEvent != null);
                    timelineItemSummary.setLastTestSuccessful(pddSectionTesting.getTesting().isPassed());


                    VHolder vHolder = calculateTimelinePddSectionSummary(entity, tlItem.getPddSection().getKey());
                    double averageTestingScore = vHolder.getValue() / vHolder.getCount();
                    timelineItemSummary.setTestsCount(vHolder.getCount());
                    timelineItemSummary.setTestsAveragePercentage(averageTestingScore);
                    timelineItemSummary.setTestsAveragePercentageFormatted(formatDouble(averageTestingScore));
                    timelineItemSummary.setStudySuccess(averageTestingScore > 90);
                    tlItem.setTimelineItemSummary(timelineItemSummary);
                });
        return result;
    }

    private static TimelineItem getLastPddSectionEvent(final String pddSectionKey, final TimeLineItemEventType eventType, final TimelineEntity entity) {
        List<PddSectionTimelineItem> timelineItems = entity.getPddSectionTimelineItems().stream()
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
        List<TimelineItem> timelineItems1 = timelineItems.get(timelineItems.size() - 1).getTimelineItems();
        return timelineItems1.get(timelineItems1.size() - 1);
    }

    private static List<TimelineDayDto> convertTimelineDaysForPddSection(final String pddSectionKey, final TimelineEntity entity, final List<TimelineDayColumn> dayColumns) {
        List<TimelineItem> pddSectionTimelineItems = filterTimelineItemsByPddSectionKey(pddSectionKey, entity.getPddSectionTimelineItems());
        return dayColumns.stream()
                .map(dayColumn -> {
                    TimelineDayDto timelineDay = new TimelineDayDto();
                    timelineDay.setDayIndex(dayColumn.getDayIndex());
                    timelineDay.setDayDate(dayColumn.getDate());

                    TimelineDayEventsDto dayEvents = new TimelineDayEventsDto();
                    populatePddSectionDayEvents(dayColumn.getDate(), pddSectionTimelineItems, dayEvents);
                    timelineDay.setDayEvents(dayEvents);

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
        String percentage = formatDouble((double) testing.getPassedQuestions() / testing.getTotalQuestions() * 100);
        return new TestingDto(testing.getPassedQuestions(), testing.getTotalQuestions(), testing.isPassed(), percentage);
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

    @Getter
    private static class VHolder {
        private double value = 0D;
        private int count = 0;

        void add(final double value) {
            this.value += value;
            this.count++;
        }
    }
}
