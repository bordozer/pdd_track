package com.pdd.track.converter;

import com.pdd.track.dto.DrivingDto;
import com.pdd.track.dto.DrivingDto.CarDto;
import com.pdd.track.dto.DrivingDto.InstructorDto;
import com.pdd.track.dto.PddSectionDto;
import com.pdd.track.dto.TestingDto;
import com.pdd.track.dto.TimelineDayDto;
import com.pdd.track.dto.TimelineDayEventsDto;
import com.pdd.track.dto.TimelineDayColumn;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.dto.TimelineItemDto;
import com.pdd.track.entity.UserStudyTimelineEntity;
import com.pdd.track.model.Car;
import com.pdd.track.model.Instructor;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.Testing;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.events.PddSectionTesting;
import com.pdd.track.model.events.SchoolDrivingEvent;
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

    public static TimelineDto toDto(final UserStudyTimelineEntity entity) {
        List<TimelineDayColumn> dayColumns = getColumns();
        TimelineDto result = new TimelineDto();
        result.setDayColumns(dayColumns);
        result.setItems(convertItems(entity, dayColumns));

        return result;
    }

    private static List<TimelineDayColumn> getColumns() {
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

    private static List<TimelineItemDto> convertItems(final UserStudyTimelineEntity entity, final List<TimelineDayColumn> dayColumns) {
        return convertPddSections(entity).stream()
                .map(sectionDto -> {
                    TimelineItemDto item = new TimelineItemDto();
                    item.setPddSection(sectionDto);
                    item.setCells(convertCells(sectionDto.getKey(), entity, dayColumns));
                    return item;
                })
                .collect(Collectors.toList());
    }

    private static List<TimelineDayDto> convertCells(final String key, final UserStudyTimelineEntity entity, final List<TimelineDayColumn> dayColumns) {

        List<TimelineItem> pddSectionTimelineItems = entity.getPddSectionTimelineItems().stream()
                .filter(section -> section.getPddSection().getKey().equals(key))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .getTimelineItems();


        List<TimelineDayDto> result = new ArrayList<>();
        result.addAll(pddSectionTimelineItems.stream()
                .map(timelineItem -> {
                    LocalDate timelineDate = timelineItem.getDate();

                    TimelineDayDto cell = new TimelineDayDto();
                    cell.setDayIndex(getDayIndex(timelineDate, dayColumns));
                    cell.setCellEvents(convertDayEvents(timelineDate, pddSectionTimelineItems));

                    return cell;
                })
                .collect(Collectors.toList())
        );

        return result;
    }

    private static TimelineDayEventsDto convertDayEvents(final LocalDate timelineDate, final List<TimelineItem> pddSectionTimelineItems) {
        TimelineDayEventsDto dayEvents = new TimelineDayEventsDto();
        pddSectionTimelineItems.stream()
                .filter(timelineItem -> timelineItem.getDate().equals(timelineDate))
                .forEach(timelineItem -> {
                    TimelineEvent event = timelineItem.getEvent();
                    switch (event.getEventType()) {
                        case LECTURE:
                            dayEvents.setLecture(true);
                            break;
                        case DRIVING:
                            SchoolDrivingEvent schoolDrivingEvent = (SchoolDrivingEvent) event;
                            CarDto carDto = convertCar(schoolDrivingEvent.getCar());
                            InstructorDto instructor = convertInstructor(schoolDrivingEvent.getInstructor());
                            dayEvents.setDriving(new DrivingDto(carDto, instructor, schoolDrivingEvent.getDuration()));
                            break;
                        case STUDY:
                            dayEvents.setStudy(true);
                            break;
                        case TESTING:
                            PddSectionTesting pddSectionTestingEvent = (PddSectionTesting) event;
                            dayEvents.setTesting(convertTestingEvent(pddSectionTestingEvent.getTesting()));
                            break;
                        default:
                            break;
                    }
                });
        return dayEvents;
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

    private static int getDayIndex(final LocalDate date, final List<TimelineDayColumn> dayColumns) {
        return dayColumns.stream()
                .filter(dayColumn -> dayColumn.getDate().isEqual(date))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .getIndex();
    }

    private static List<PddSectionDto> convertPddSections(final UserStudyTimelineEntity entity) {
        return entity.getPddSectionTimelineItems().stream()
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
