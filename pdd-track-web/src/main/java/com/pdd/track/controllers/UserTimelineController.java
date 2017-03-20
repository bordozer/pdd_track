package com.pdd.track.controllers;

import com.google.common.collect.Lists;
import com.pdd.track.entity.UserStudyTimelineEntity;
import com.pdd.track.model.Car;
import com.pdd.track.model.Gender;
import com.pdd.track.model.Instructor;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.PddSectionTimelineItem;
import com.pdd.track.model.StudyingTimelineItem;
import com.pdd.track.model.Student;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.events.LectureEvent;
import com.pdd.track.model.events.PddSectionStudy;
import com.pdd.track.model.events.PddSectionTesting;
import com.pdd.track.model.events.SchoolDrivingEvent;
import com.pdd.track.service.TimelineService;
import com.pdd.track.utils.RandomUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/timeline")
public class UserTimelineController {

    private static final Student STUDENT = Student.builder().key("qaz-wsx-edc").name("BorDark").gender(Gender.MALE).build(); // TODO: read user from context

    private static final Instructor SCHOOL_INSTRUCTOR = new Instructor("Sergei");
    private static final Car SCHOOL_CAR = new Car("Toyota RAW4");
    private static final Instructor ADDITIONAL_INSTRUCTOR = new Instructor("Yuriy");
    private static final Car ADDITIONAL_CAR = new Car("Honda Accord");

    private static final PddSection PDD_SECTION_01 = new PddSection("1", "Obschie polozheniya", 116);
    private static final PddSection PDD_SECTION_02 = new PddSection("2", "Obyazannosti i prava voditeley mehanicheskih transportnyih sredstv", 85);
    private static final PddSection PDD_SECTION_03 = new PddSection("3", "Dvizhenie transportnyh sredstv so special'nymi signalami", 150);
    private static final PddSection PDD_SECTION_04 = new PddSection("4", "Obyazannosti i prava peshekhodov", 28);
    private static final PddSection PDD_SECTION_05 = new PddSection("5", "Obyazannosti i prava passazhirov", 12);
    private static final PddSection PDD_SECTION_06 = new PddSection("6", "Trebovaniya k velosipedistam", 15);
    private static final PddSection PDD_SECTION_07 = new PddSection("7", "Trebovaniya k licam, upravlyayushchim guzhevym transportom i pogonshchikam zhivotnyh", 6);
    private static final PddSection PDD_SECTION_08 = new PddSection("8", "Regulirovanie dorozhnogo dvizheniya", 239);
    private static final PddSection PDD_SECTION_09 = new PddSection("9", "Preduprezhdayushchie signaly", 44);
    private static final PddSection PDD_SECTION_10 = new PddSection("10", "Nachalo dvizheniya i izmenenie ego napravleniya", 67);
    private static final PddSection PDD_SECTION_11 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_12 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_13 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_14 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_15 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_16 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_17 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_18 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_19 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_20 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_21 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_22 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_23 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_24 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_25 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_26 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_27 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_28 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_29 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_30 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_31 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_32 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_33 = new PddSection("1", "General", 116);
    private static final PddSection PDD_SECTION_34 = new PddSection("1", "General", 116);


    @Inject
    private TimelineService timelineService;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public List<UserStudyTimelineEntity> getAll() {
        return timelineService.getAll(STUDENT.getKey());
    }

    // TODO: for dev purposes only
    @RequestMapping(method = RequestMethod.PUT, value = "/data-create/")
    public UserStudyTimelineEntity createLogRecord() {
        timelineService.deleteAll();
        return timelineService.create(constructUserStudyTimelineEntity());
    }

    private UserStudyTimelineEntity constructUserStudyTimelineEntity() {
        UserStudyTimelineEntity entity = new UserStudyTimelineEntity();
        entity.setStudent(STUDENT);
        entity.setStudyingTimelineItems(constructStudyingTimelineItems());
        entity.setPddSectionTimelineItems(constructPddSectionTimelineItems());
        return entity;
    }

    private List<StudyingTimelineItem> constructStudyingTimelineItems() {
        return Lists.newArrayList(
                new StudyingTimelineItem(new TimelineItem(LocalDate.of(2017, 3, 17), new SchoolDrivingEvent(SCHOOL_INSTRUCTOR, SCHOOL_CAR, 50))),
                new StudyingTimelineItem(new TimelineItem(LocalDate.of(2017, 3, 18), new SchoolDrivingEvent(ADDITIONAL_INSTRUCTOR, ADDITIONAL_CAR, 90))),
                new StudyingTimelineItem(new TimelineItem(LocalDate.of(2017, 3, 19), new SchoolDrivingEvent(ADDITIONAL_INSTRUCTOR, ADDITIONAL_CAR, 90)))
        );
    }

    private List<PddSectionTimelineItem> constructPddSectionTimelineItems() {

        TimelineItem lecture1 = new TimelineItem(LocalDate.of(2017, 3, 4), new LectureEvent());
        TimelineItem study1 = new TimelineItem(LocalDate.of(2017, 3, 6), new PddSectionStudy());
        TimelineItem testing1 = new TimelineItem(LocalDate.of(2017, 3, 7), new PddSectionTesting(123, 136, true));
        TimelineItem testing2 = new TimelineItem(LocalDate.of(2017, 3, 9), new PddSectionTesting(32, 34, true));
        TimelineItem testing3 = new TimelineItem(LocalDate.of(2017, 3, 14), new PddSectionTesting(107, 116, true));
        TimelineItem testing4 = new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(75, 80, true));

        PddSectionTimelineItem sectionTimelineItem = new PddSectionTimelineItem();
        sectionTimelineItem.setPddSection(PDD_SECTION_01);
        sectionTimelineItem.setTimelineItems(Lists.newArrayList(lecture1, study1, testing1, testing2, testing3, testing4));

        return Lists.newArrayList(sectionTimelineItem);
    }
}
