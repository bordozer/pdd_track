package com.pdd.track.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pdd.track.entity.TimelineEntity;
import com.pdd.track.model.Car;
import com.pdd.track.model.Gender;
import com.pdd.track.model.Instructor;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.PddSectionTimelineItem;
import com.pdd.track.model.Student;
import com.pdd.track.model.StudyingTimelineItem;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.events.AdditionalDrivingEvent;
import com.pdd.track.model.events.LectureEvent;
import com.pdd.track.model.events.PddSectionStudy;
import com.pdd.track.model.events.PddSectionTesting;
import com.pdd.track.model.events.SchoolDrivingEvent;
import com.pdd.track.service.DataGenerationService;
import com.pdd.track.service.TimelineService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class DataGenerationServiceImpl implements DataGenerationService {

    public static final Student STUDENT = Student.builder().key("qaz-wsx-edc").name("BorDark").gender(Gender.MALE).build(); // TODO: read user from context

    private static final Instructor SCHOOL_INSTRUCTOR = new Instructor("Sergei");
    private static final Car SCHOOL_CAR = new Car("Toyota RAV4");

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
    private static final PddSection PDD_SECTION_11 = new PddSection("11", "General", 116);
    private static final PddSection PDD_SECTION_12 = new PddSection("12", "General", 116);
    private static final PddSection PDD_SECTION_13 = new PddSection("13", "General", 116);
    private static final PddSection PDD_SECTION_14 = new PddSection("14", "General", 116);
    private static final PddSection PDD_SECTION_15 = new PddSection("15", "Ostanovka i stoyanka", 116);
    private static final PddSection PDD_SECTION_16 = new PddSection("16", "General", 116);
    private static final PddSection PDD_SECTION_17 = new PddSection("17", "General", 116);
    private static final PddSection PDD_SECTION_18 = new PddSection("18", "General", 116);
    private static final PddSection PDD_SECTION_19 = new PddSection("19", "General", 116);
    private static final PddSection PDD_SECTION_20 = new PddSection("20", "General", 116);
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
    private static final PddSection PDD_SECTION_33 = new PddSection("33", "Znaki", 116);
    private static final PddSection PDD_SECTION_34 = new PddSection("1", "General", 116);

    public static final LocalDate STUDY_START_DAY = LocalDate.of(2017, 2, 25);
    public static final LocalDate STUDY_END_DAY = LocalDate.of(2017, 4, 23);

    public static final Set<DayOfWeek> LECTURE_WEEK_DAYS = Sets.newHashSet(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    @Inject
    private TimelineService timelineService;

    @Override
    public TimelineEntity createData() {
        timelineService.deleteAll();
        return timelineService.create(constructUserStudyTimelineEntity());
    }

    private TimelineEntity constructUserStudyTimelineEntity() {
        TimelineEntity entity = new TimelineEntity();
        entity.setStudent(STUDENT);
        entity.setStudyingTimelineItems(constructStudyingTimelineItems());
        entity.setPddSectionTimelineItems(constructPddSectionTimelineItems());
        return entity;
    }

    private List<StudyingTimelineItem> constructStudyingTimelineItems() {
        return Lists.newArrayList(
                new StudyingTimelineItem(new TimelineItem(LocalDate.of(2017, 3, 17), new SchoolDrivingEvent(SCHOOL_INSTRUCTOR, SCHOOL_CAR, 50))),
                new StudyingTimelineItem(new TimelineItem(LocalDate.of(2017, 3, 18), new AdditionalDrivingEvent(ADDITIONAL_INSTRUCTOR, ADDITIONAL_CAR, 90))),
                new StudyingTimelineItem(new TimelineItem(LocalDate.of(2017, 3, 19), new AdditionalDrivingEvent(ADDITIONAL_INSTRUCTOR, ADDITIONAL_CAR, 90))),
                new StudyingTimelineItem(new TimelineItem(LocalDate.of(2017, 3, 21), new SchoolDrivingEvent(SCHOOL_INSTRUCTOR, SCHOOL_CAR, 110)))
        );
    }

    private List<PddSectionTimelineItem> constructPddSectionTimelineItems() {

        // 1
        TimelineItem lecture1 = new TimelineItem(LocalDate.of(2017, 3, 4), new LectureEvent());
        TimelineItem lecture1_2 = new TimelineItem(LocalDate.of(2017, 3, 18), new LectureEvent());
        TimelineItem lecture1_3 = new TimelineItem(LocalDate.of(2017, 3, 19), new LectureEvent());
        TimelineItem study1 = new TimelineItem(LocalDate.of(2017, 3, 6), new PddSectionStudy());

        TimelineItem testing1_1 = new TimelineItem(LocalDate.of(2017, 3, 7), new PddSectionTesting(123, 136, true));
        TimelineItem testing1_2 = new TimelineItem(LocalDate.of(2017, 3, 9), new PddSectionTesting(32, 34, true));
        TimelineItem testing1_3 = new TimelineItem(LocalDate.of(2017, 3, 14), new PddSectionTesting(107, 116, true));
        TimelineItem testing1_4 = new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(75, 80, true));

        PddSectionTimelineItem sectionTimelineItem1 = new PddSectionTimelineItem();
        sectionTimelineItem1.setPddSection(PDD_SECTION_01);
        sectionTimelineItem1.setTimelineItems(Lists.newArrayList(lecture1, study1, testing1_1, testing1_2, testing1_3, testing1_4, lecture1_2, lecture1_3));

        // 2
        TimelineItem lecture2 = new TimelineItem(LocalDate.of(2017, 2, 25), new LectureEvent());
        TimelineItem study2 = new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionStudy());
        TimelineItem testing2_1 = new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(76, 85, false));
        TimelineItem testing2_2 = new TimelineItem(LocalDate.of(2017, 3, 10), new PddSectionTesting(81, 85, true));
        TimelineItem testing2_3 = new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(81, 85, true));

        PddSectionTimelineItem sectionTimelineItem2 = new PddSectionTimelineItem();
        sectionTimelineItem2.setPddSection(PDD_SECTION_02);
        sectionTimelineItem2.setTimelineItems(Lists.newArrayList(lecture2, study2, testing2_1, testing2_2, testing2_3));

        // 3
        PddSectionTimelineItem sectionTimelineItem3 = new PddSectionTimelineItem();
        sectionTimelineItem3.setPddSection(PDD_SECTION_03);
        sectionTimelineItem3.setTimelineItems(Lists.newArrayList());

        // 4
        TimelineItem lecture4 = new TimelineItem(LocalDate.of(2017, 3, 4), new LectureEvent());
        TimelineItem study4 = new TimelineItem(LocalDate.of(2017, 3, 6), new PddSectionStudy());
        TimelineItem testing4_1 = new TimelineItem(LocalDate.of(2017, 3, 7), new PddSectionTesting(17, 20, false));
        TimelineItem testing4_2 = new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(26, 28, true));
        TimelineItem testing4_3 = new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(27, 28, true));
        TimelineItem testing4_4 = new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(28, 28, true));

        PddSectionTimelineItem sectionTimelineItem4 = new PddSectionTimelineItem();
        sectionTimelineItem4.setPddSection(PDD_SECTION_04);
        sectionTimelineItem4.setTimelineItems(Lists.newArrayList(lecture4, study4, testing4_1, testing4_2, testing4_3, testing4_4));

        // 15
        TimelineItem lecture15 = new TimelineItem(LocalDate.of(2017, 3, 12), new LectureEvent());

        PddSectionTimelineItem sectionTimelineItem15 = new PddSectionTimelineItem();
        sectionTimelineItem15.setPddSection(PDD_SECTION_15);
        sectionTimelineItem15.setTimelineItems(Lists.newArrayList(lecture15));

        // 33
        TimelineItem lecture33 = new TimelineItem(LocalDate.of(2017, 3, 18), new LectureEvent());
        TimelineItem study33 = new TimelineItem(LocalDate.of(2017, 3, 19), new PddSectionStudy());
        TimelineItem testing33_1 = new TimelineItem(LocalDate.of(2017, 3, 20), new PddSectionTesting(314, 368, false));
        TimelineItem testing33_2 = new TimelineItem(LocalDate.of(2017, 3, 22), new PddSectionTesting(141, 153, true));

        PddSectionTimelineItem sectionTimelineItem33 = new PddSectionTimelineItem();
        sectionTimelineItem33.setPddSection(PDD_SECTION_33);
        sectionTimelineItem33.setTimelineItems(Lists.newArrayList(lecture33, study33, testing33_1, testing33_2));

        return Lists.newArrayList(
                sectionTimelineItem1,
                sectionTimelineItem2,
                sectionTimelineItem3,
                sectionTimelineItem4,
                sectionTimelineItem15,
                sectionTimelineItem33
        );
    }
}
