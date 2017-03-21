package com.pdd.track.converter;

import com.pdd.track.dto.DrivingDto;
import com.pdd.track.dto.DrivingDto.CarDto;
import com.pdd.track.dto.DrivingDto.InstructorDto;
import com.pdd.track.dto.PddSectionDto;
import com.pdd.track.dto.TestingDto;
import com.pdd.track.dto.TimelineDayColumn;
import com.pdd.track.dto.TimelineDayDto;
import com.pdd.track.dto.TimelineDayEventsDto;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.dto.TimelineItemDto;
import com.pdd.track.entity.TimelineEntity;
import com.pdd.track.model.Car;
import com.pdd.track.model.Instructor;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.PddSectionTimelineItem;
import com.pdd.track.model.StudyingTimelineItem;
import com.pdd.track.model.Testing;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.events.AbstractDrivingEvent;
import com.pdd.track.model.events.AdditionalDrivingEvent;
import com.pdd.track.model.events.PddSectionTesting;
import com.pdd.track.model.events.TimelineEvent;
import com.pdd.track.service.impl.DataGenerationServiceImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimelineConverter {

    public static TimelineDto toDto(final TimelineEntity entity) {
        List<TimelineDayColumn> dayColumns = getDayColumns();
        TimelineDto result = new TimelineDto();
        result.setDayColumns(dayColumns);
        result.setItems(convertItems(entity, dayColumns));

        return result;
    }

    private static List<TimelineDayColumn> getDayColumns() {
        int index = 1;
        LocalDate currentDay = DataGenerationServiceImpl.STUDY_START_DAY;
        List<TimelineDayColumn> result = new ArrayList<>();
        while (currentDay.isBefore(DataGenerationServiceImpl.STUDY_END_DAY.plusDays(1))) {
            TimelineDayColumn dayColumn = new TimelineDayColumn();
            dayColumn.setIndex(index);
            dayColumn.setDate(currentDay);
            result.add(dayColumn);

            currentDay = currentDay.plusDays(1);
            index++;
        }
        return result;
    }

    private static List<TimelineItemDto> convertItems(final TimelineEntity entity, final List<TimelineDayColumn> dayColumns) {
        return groupPddSections(entity.getPddSectionTimelineItems()).stream()
                .map(sectionDto -> {
                    TimelineItemDto item = new TimelineItemDto();
                    item.setPddSection(sectionDto);
                    item.setTimelineDays(convertTimelineDaysForPddSection(sectionDto.getKey(), entity, dayColumns));
                    return item;
                })
                .collect(Collectors.toList());
    }

    private static List<TimelineDayDto> convertTimelineDaysForPddSection(final String pddSectionKey, final TimelineEntity entity, final List<TimelineDayColumn> dayColumns) {
        List<TimelineItem> pddSectionTimelineItems = filterTimelineItemsByPddSectionKey(pddSectionKey, entity.getPddSectionTimelineItems());
        return dayColumns.stream()
                .map(dayColumn -> {
                    TimelineDayDto timelineDay = new TimelineDayDto();
                    timelineDay.setDayIndex(dayColumn.getIndex());
                    timelineDay.setDayDate(dayColumn.getDate());

                    TimelineDayEventsDto dayEvents = new TimelineDayEventsDto();
                    populatePddSectionDayEvents(dayColumn.getDate(), pddSectionTimelineItems, dayEvents);
                    populateGlobalDayEvents(dayColumn.getDate(), entity.getStudyingTimelineItems(), dayEvents);
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

    private static void populateGlobalDayEvents(final LocalDate timelineDate, final List<StudyingTimelineItem> studyingTimelineItems, final TimelineDayEventsDto dayEvents) {
        studyingTimelineItems.stream()
                .filter(timelineItem -> timelineItem.getTimelineItem().getDate().equals(timelineDate))
                .forEach(timelineItem -> {
                    TimelineEvent event = timelineItem.getTimelineItem().getEvent();
                    switch (event.getEventType()) {
                        case DRIVING:
                            AbstractDrivingEvent schoolDrivingEvent = (AbstractDrivingEvent) event;
                            CarDto carDto = convertCar(schoolDrivingEvent.getCar());
                            InstructorDto instructor = convertInstructor(schoolDrivingEvent.getInstructor());
                            boolean additionalDriving = event instanceof AdditionalDrivingEvent;
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
        return new TestingDto(testing.getPassedQuestions(), testing.getTotalQuestions(), testing.isPassed());
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
}
