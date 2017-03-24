package com.pdd.track.service.impl;

import com.google.common.collect.Sets;
import com.pdd.track.model.SchoolTimeline;
import com.pdd.track.model.Car;
import com.pdd.track.model.Gender;
import com.pdd.track.model.Instructor;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.PddSectionTimelineItem;
import com.pdd.track.model.Student;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.PddSectionTimeline;
import com.pdd.track.model.events.AdditionalDrivingEvent;
import com.pdd.track.model.events.LectureEvent;
import com.pdd.track.model.events.LectureStudyEvent;
import com.pdd.track.model.events.PddSectionTesting;
import com.pdd.track.model.events.SchoolDrivingEvent;
import com.pdd.track.repository.PddSectionRepository;
import com.pdd.track.repository.TimelineRepository;
import com.pdd.track.repository.TimelineStudyRepository;
import com.pdd.track.service.DataGenerationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DataGenerationServiceImpl implements DataGenerationService {

    public static final Student STUDENT = Student.builder().key("qaz-wsx-edc").name("BorDark").gender(Gender.MALE).build(); // TODO: read user from context

    private static final Instructor SCHOOL_INSTRUCTOR = new Instructor("Sergei");
    private static final Car SCHOOL_CAR = new Car("Toyota RAV4");

    private static final Instructor ADDITIONAL_INSTRUCTOR = new Instructor("Yuriy");
    private static final Car ADDITIONAL_CAR = new Car("Honda Accord");

    private static final PddSection PDD_SECTION_01 = new PddSection("1", "Общие положения");
    private static final PddSection PDD_SECTION_02 = new PddSection("2", "Obyazannosti i prava voditeley TS");
    private static final PddSection PDD_SECTION_03 = new PddSection("3", "Dvizhenie TS so special'nymi signalami");
    private static final PddSection PDD_SECTION_04 = new PddSection("4", "Obyazannosti i prava peshekhodov");
    private static final PddSection PDD_SECTION_05 = new PddSection("5", "Obyazannosti i prava passazhirov");
    private static final PddSection PDD_SECTION_06 = new PddSection("6", "Trebovaniya k velosipedistam");
    private static final PddSection PDD_SECTION_07 = new PddSection("7", "Trebovaniya k licam, upravlyayushchim guzhevym transportom i pogonshchikam zhivotnyh");
    private static final PddSection PDD_SECTION_08 = new PddSection("8", "Regulirovanie dorozhnogo dvizheniya");
    private static final PddSection PDD_SECTION_09 = new PddSection("9", "Preduprezhdayushchie signaly");
    private static final PddSection PDD_SECTION_10 = new PddSection("10", "Nachalo dvizheniya i izmenenie ego napravleniya");
    private static final PddSection PDD_SECTION_11 = new PddSection("11", "Raspolozhenie transportnyh sredstv na doroge");
    private static final PddSection PDD_SECTION_12 = new PddSection("12", "Skorost' dvizheniya");
    private static final PddSection PDD_SECTION_13 = new PddSection("13", "Distanciya, interval, vstrechnyj raz'ezd");
    private static final PddSection PDD_SECTION_14 = new PddSection("14", "Obgon");
    private static final PddSection PDD_SECTION_15 = new PddSection("15", "Ostanovka i stoyanka");
    private static final PddSection PDD_SECTION_16 = new PddSection("16", "Proezd perekrestkov");
    private static final PddSection PDD_SECTION_17 = new PddSection("17", "Preimushchestva marshrutnyh transportnyh sredstv");
    private static final PddSection PDD_SECTION_18 = new PddSection("18", "Proezd peshekhodnyh perekhodov i ostanovok transportnyh sredstv");
    private static final PddSection PDD_SECTION_19 = new PddSection("19", "Ispol'zovanie vneshnih svetovyh priborov");
    private static final PddSection PDD_SECTION_20 = new PddSection("20", "Dvizhenie cherez zheleznodorozhnye pereezdy");
    private static final PddSection PDD_SECTION_21 = new PddSection("21", "Perevozka passazhirov");
    private static final PddSection PDD_SECTION_22 = new PddSection("22", "Perevozka gruza");
    private static final PddSection PDD_SECTION_23 = new PddSection("23", "Buksirovka i ehkspluataciya transportnyh sostavov");
    private static final PddSection PDD_SECTION_24 = new PddSection("24", "Uchebnaya ezda");
    private static final PddSection PDD_SECTION_25 = new PddSection("25", "Dvizhenie transportnyh sredstv v kolonnah");
    private static final PddSection PDD_SECTION_26 = new PddSection("26", "Dvizhenie v zhiloj i peshekhodnoj zone");
    private static final PddSection PDD_SECTION_27 = new PddSection("27", "Dvizhenie po avtomagistralyam i dorogam dlya avtomobilej");
    private static final PddSection PDD_SECTION_28 = new PddSection("28", "Dvizhenie po gornym dorogam i na krutyh spuskah");
    private static final PddSection PDD_SECTION_29 = new PddSection("29", "Mezhdunarodnoe dvizhenie");
    private static final PddSection PDD_SECTION_30 = new PddSection("30", "Nomernye, opoznavatel'nye znaki, nadpisi i oboznacheniya");
    private static final PddSection PDD_SECTION_31 = new PddSection("31", "Tekhnicheskoe sostoyanie transportnyh sredstv i ih osnashchenie");
    private static final PddSection PDD_SECTION_32 = new PddSection("32", "Voprosy dorozhnogo dvizheniya, kotorye trebuyut soglasovaniya s GAI");
    private static final PddSection PDD_SECTION_33 = new PddSection("33", "Znaki");
    private static final PddSection PDD_SECTION_34 = new PddSection("34", "Dorozhnaya razmetka");

    private static final Map<String, Integer> QUESTIONS_COUNT_KIEV = new HashMap<>();
    static {
        QUESTIONS_COUNT_KIEV.put("1", 116);
        QUESTIONS_COUNT_KIEV.put("2", 85);
        QUESTIONS_COUNT_KIEV.put("3", 150);
        QUESTIONS_COUNT_KIEV.put("4", 28);
        QUESTIONS_COUNT_KIEV.put("5", 12);
        QUESTIONS_COUNT_KIEV.put("6", 15);
        QUESTIONS_COUNT_KIEV.put("7", 6);
        QUESTIONS_COUNT_KIEV.put("8", 239);
        QUESTIONS_COUNT_KIEV.put("9", 44);
        QUESTIONS_COUNT_KIEV.put("10", 67);
        QUESTIONS_COUNT_KIEV.put("11", 53);
        QUESTIONS_COUNT_KIEV.put("12", 64);
        QUESTIONS_COUNT_KIEV.put("13", 14);
        QUESTIONS_COUNT_KIEV.put("14", 78);
        QUESTIONS_COUNT_KIEV.put("15", 103);
        QUESTIONS_COUNT_KIEV.put("16", 259);
        QUESTIONS_COUNT_KIEV.put("17", 9);
        QUESTIONS_COUNT_KIEV.put("18", 29);
        QUESTIONS_COUNT_KIEV.put("19", 15);
        QUESTIONS_COUNT_KIEV.put("20", 32);
        QUESTIONS_COUNT_KIEV.put("21", 11);
        QUESTIONS_COUNT_KIEV.put("22", 14);
        QUESTIONS_COUNT_KIEV.put("23", 34);
        QUESTIONS_COUNT_KIEV.put("24", 9);
        QUESTIONS_COUNT_KIEV.put("25", 8);
        QUESTIONS_COUNT_KIEV.put("26", 18);
        QUESTIONS_COUNT_KIEV.put("27", 12);
        QUESTIONS_COUNT_KIEV.put("28", 12);
        QUESTIONS_COUNT_KIEV.put("29", 3);
        QUESTIONS_COUNT_KIEV.put("30", 24);
        QUESTIONS_COUNT_KIEV.put("31", 71);
        QUESTIONS_COUNT_KIEV.put("32", 6);
        QUESTIONS_COUNT_KIEV.put("33", 368);
        QUESTIONS_COUNT_KIEV.put("34", 46);
    }

    private static final Map<String, Integer> QUESTIONS_COUNT_KHARKOV = new HashMap<>();
    static {
        QUESTIONS_COUNT_KHARKOV.put("1", );
        QUESTIONS_COUNT_KHARKOV.put("2", );
        QUESTIONS_COUNT_KHARKOV.put("3", );
        QUESTIONS_COUNT_KHARKOV.put("4", );
        QUESTIONS_COUNT_KHARKOV.put("5", );
        QUESTIONS_COUNT_KHARKOV.put("6", );
        QUESTIONS_COUNT_KHARKOV.put("7", );
        QUESTIONS_COUNT_KHARKOV.put("8", );
        QUESTIONS_COUNT_KHARKOV.put("9", );
        QUESTIONS_COUNT_KHARKOV.put("10", );
        QUESTIONS_COUNT_KHARKOV.put("11", );
        QUESTIONS_COUNT_KHARKOV.put("12", );
        QUESTIONS_COUNT_KHARKOV.put("13", );
        QUESTIONS_COUNT_KHARKOV.put("14", );
        QUESTIONS_COUNT_KHARKOV.put("15", );
        QUESTIONS_COUNT_KHARKOV.put("16", );
        QUESTIONS_COUNT_KHARKOV.put("17", );
        QUESTIONS_COUNT_KHARKOV.put("18", );
        QUESTIONS_COUNT_KHARKOV.put("19", );
        QUESTIONS_COUNT_KHARKOV.put("20", );
        QUESTIONS_COUNT_KHARKOV.put("21", );
        QUESTIONS_COUNT_KHARKOV.put("22", );
        QUESTIONS_COUNT_KHARKOV.put("23", );
        QUESTIONS_COUNT_KHARKOV.put("24", );
        QUESTIONS_COUNT_KHARKOV.put("25", );
        QUESTIONS_COUNT_KHARKOV.put("26", );
        QUESTIONS_COUNT_KHARKOV.put("27", );
        QUESTIONS_COUNT_KHARKOV.put("28", );
        QUESTIONS_COUNT_KHARKOV.put("29", );
        QUESTIONS_COUNT_KHARKOV.put("30", );
        QUESTIONS_COUNT_KHARKOV.put("31", );
        QUESTIONS_COUNT_KHARKOV.put("32", );
        QUESTIONS_COUNT_KHARKOV.put("33", );
    }

    public static final LocalDate STUDY_START_DAY = LocalDate.of(2017, 2, 25);
    public static final LocalDate STUDY_END_DAY = LocalDate.of(2017, 4, 23);

    public static final Set<DayOfWeek> LECTURE_WEEK_DAYS = Sets.newHashSet(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    @Inject
    private PddSectionRepository pddSectionRepository;

    @Inject
    private TimelineRepository timelineRepository;

    @Inject
    private TimelineStudyRepository timelineStudyRepository;

    @Override
    public void createData() {
        pddSectionRepository.deleteAll();
        timelineRepository.deleteAll();
        timelineStudyRepository.deleteAll();
        createPddSections();
        SchoolTimeline schoolTimeline = timelineRepository.save(constructTimeline());
        PddSectionTimeline studyKiev = timelineStudyRepository.save(constructTimelineStudyKiev(schoolTimeline.get_id()));
        PddSectionTimeline studyKharkov = timelineStudyRepository.save(constructTimelineStudyKharkov(schoolTimeline.get_id()));
    }

    private SchoolTimeline constructTimeline() {
        SchoolTimeline entity = new SchoolTimeline();
        entity.setStudent(STUDENT);
        entity.setTimelineItems(constructStudyingTimelineItems());
        return entity;
    }

    private PddSectionTimeline constructTimelineStudyKiev(final String timelineId) {
        PddSectionTimeline pddSectionTimeline = new PddSectionTimeline();
        pddSectionTimeline.set_id("1");
        pddSectionTimeline.setSchoolTimelineId(timelineId);
        pddSectionTimeline.setTimelineItems(constructPddSectionTimelineItemsKiev());
        pddSectionTimeline.setQuestionsCountMap(QUESTIONS_COUNT_KIEV);
        return pddSectionTimeline;
    }

    private PddSectionTimeline constructTimelineStudyKharkov(final String timelineId) {
        PddSectionTimeline pddSectionTimeline = new PddSectionTimeline();
        pddSectionTimeline.set_id("2");
        pddSectionTimeline.setSchoolTimelineId(timelineId);
        pddSectionTimeline.setTimelineItems(constructPddSectionTimelineItemsKharkov());
        pddSectionTimeline.setQuestionsCountMap(QUESTIONS_COUNT_KHARKOV);
        return pddSectionTimeline;
    }

    private List<PddSectionTimelineItem> constructPddSectionTimelineItemsKharkov() {
        return Arrays.asList(
                new PddSectionTimelineItem(PDD_SECTION_07, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 24), new PddSectionTesting(1, 1, true))

                ))
        );
    }

    private List<TimelineItem> constructStudyingTimelineItems() {
        return Arrays.asList(
                new TimelineItem(LocalDate.of(2017, 3, 4), new LectureEvent(PDD_SECTION_01.getKey())),
                new TimelineItem(LocalDate.of(2017, 2, 25), new LectureEvent(PDD_SECTION_02.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 4), new LectureEvent(PDD_SECTION_04.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 4), new LectureEvent(PDD_SECTION_05.getKey())),
                new TimelineItem(LocalDate.of(2017, 2, 25), new LectureEvent(PDD_SECTION_06.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 4), new LectureEvent(PDD_SECTION_07.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 5), new LectureEvent(PDD_SECTION_09.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 11), new LectureEvent(PDD_SECTION_10.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 12), new LectureEvent(PDD_SECTION_11.getKey())),
                new TimelineItem(LocalDate.of(2017, 2, 26), new LectureEvent(PDD_SECTION_12.getKey())),
                new TimelineItem(LocalDate.of(2017, 2, 26), new LectureEvent(PDD_SECTION_13.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 12), new LectureEvent(PDD_SECTION_14.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 12), new LectureEvent(PDD_SECTION_15.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 11), new LectureEvent(PDD_SECTION_17.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 12), new LectureEvent(PDD_SECTION_18.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 5), new LectureEvent(PDD_SECTION_19.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 5), new LectureEvent(PDD_SECTION_19.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 5), new LectureEvent(PDD_SECTION_20.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 12), new LectureEvent(PDD_SECTION_21.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 4), new LectureEvent(PDD_SECTION_22.getKey())),
                new TimelineItem(LocalDate.of(2017, 2, 26), new LectureEvent(PDD_SECTION_23.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 5), new LectureEvent(PDD_SECTION_25.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 5), new LectureEvent(PDD_SECTION_26.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 18), new LectureEvent(PDD_SECTION_27.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 18), new LectureEvent(PDD_SECTION_28.getKey())),
                new TimelineItem(LocalDate.of(2017, 2, 26), new LectureEvent(PDD_SECTION_30.getKey())),
                new TimelineItem(LocalDate.of(2017, 2, 25), new LectureEvent(PDD_SECTION_31.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 18), new LectureEvent(PDD_SECTION_33.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 19), new LectureEvent(PDD_SECTION_33.getKey())),

                new TimelineItem(LocalDate.of(2017, 3, 6), new LectureStudyEvent(PDD_SECTION_01.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 8), new LectureStudyEvent(PDD_SECTION_02.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 6), new LectureStudyEvent(PDD_SECTION_04.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 23), new LectureStudyEvent(PDD_SECTION_04.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 6), new LectureStudyEvent(PDD_SECTION_05.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 23), new LectureStudyEvent(PDD_SECTION_05.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 6), new LectureStudyEvent(PDD_SECTION_06.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 23), new LectureStudyEvent(PDD_SECTION_06.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 6), new LectureStudyEvent(PDD_SECTION_07.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 23), new LectureStudyEvent(PDD_SECTION_07.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 6), new LectureStudyEvent(PDD_SECTION_09.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 23), new LectureStudyEvent(PDD_SECTION_09.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 13), new LectureStudyEvent(PDD_SECTION_10.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 23), new LectureStudyEvent(PDD_SECTION_10.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 15), new LectureStudyEvent(PDD_SECTION_11.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 8), new LectureStudyEvent(PDD_SECTION_12.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 8), new LectureStudyEvent(PDD_SECTION_13.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 15), new LectureStudyEvent(PDD_SECTION_14.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 16), new PddSectionTesting(78, 78, true)),
                new TimelineItem(LocalDate.of(2017, 3, 12), new LectureStudyEvent(PDD_SECTION_17.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 23), new LectureStudyEvent(PDD_SECTION_17.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 13), new LectureStudyEvent(PDD_SECTION_18.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 7), new LectureStudyEvent(PDD_SECTION_19.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 8), new LectureStudyEvent(PDD_SECTION_20.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 13), new LectureStudyEvent(PDD_SECTION_21.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 6), new LectureStudyEvent(PDD_SECTION_22.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 7), new LectureStudyEvent(PDD_SECTION_23.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 8), new LectureStudyEvent(PDD_SECTION_25.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 8), new LectureStudyEvent(PDD_SECTION_26.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 19), new LectureStudyEvent(PDD_SECTION_27.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 19), new LectureStudyEvent(PDD_SECTION_28.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 9), new LectureStudyEvent(PDD_SECTION_30.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 20), new LectureStudyEvent(PDD_SECTION_31.getKey())),
                new TimelineItem(LocalDate.of(2017, 3, 19), new LectureStudyEvent(PDD_SECTION_33.getKey())),

                new TimelineItem(LocalDate.of(2017, 3, 17), new SchoolDrivingEvent(SCHOOL_INSTRUCTOR, SCHOOL_CAR, 50)),
                new TimelineItem(LocalDate.of(2017, 3, 18), new AdditionalDrivingEvent(ADDITIONAL_INSTRUCTOR, ADDITIONAL_CAR, 90)),
                new TimelineItem(LocalDate.of(2017, 3, 19), new AdditionalDrivingEvent(ADDITIONAL_INSTRUCTOR, ADDITIONAL_CAR, 90)),
                new TimelineItem(LocalDate.of(2017, 3, 21), new SchoolDrivingEvent(SCHOOL_INSTRUCTOR, SCHOOL_CAR, 110)),
                new TimelineItem(LocalDate.of(2017, 3, 24), new AdditionalDrivingEvent(ADDITIONAL_INSTRUCTOR, ADDITIONAL_CAR, 90))
        );
    }

    private List<PddSectionTimelineItem> constructPddSectionTimelineItemsKiev() {
        return Arrays.asList(
                new PddSectionTimelineItem(PDD_SECTION_01, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 7), new PddSectionTesting(123, 136, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 9), new PddSectionTesting(32, 34, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 14), new PddSectionTesting(107, 116, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(75, 80, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 23), new PddSectionTesting(111, 116, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_02, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(76, 85, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 10), new PddSectionTesting(81, 85, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(81, 85, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 23), new PddSectionTesting(83, 85, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_03, Arrays.asList(

                )),
                new PddSectionTimelineItem(PDD_SECTION_04, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 7), new PddSectionTesting(17, 20, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(26, 28, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(27, 28, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(28, 28, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 23), new PddSectionTesting(27, 28, true))

                )),
                new PddSectionTimelineItem(PDD_SECTION_05, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 7), new PddSectionTesting(10, 12, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(10, 12, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 9), new PddSectionTesting(12, 12, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 10), new PddSectionTesting(12, 12, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(12, 12, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(12, 12, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 16), new PddSectionTesting(12, 12, true))

                )),
                new PddSectionTimelineItem(PDD_SECTION_06, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 7), new PddSectionTesting(13, 15, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(15, 15, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 10), new PddSectionTesting(15, 15, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(15, 15, true))

                )),
                new PddSectionTimelineItem(PDD_SECTION_07, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 7), new PddSectionTesting(5, 6, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(5, 6, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 9), new PddSectionTesting(6, 6, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 10), new PddSectionTesting(6, 6, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(6, 6, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(6, 6, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 16), new PddSectionTesting(6, 6, true))

                )),
                new PddSectionTimelineItem(PDD_SECTION_08, Arrays.asList(

                )),
                new PddSectionTimelineItem(PDD_SECTION_09, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 7), new PddSectionTesting(18, 20, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(39, 44, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 14), new PddSectionTesting(43, 44, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(44, 44, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 24), new PddSectionTesting(42, 44, true))

                )),
                new PddSectionTimelineItem(PDD_SECTION_10, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(55, 67, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 14), new PddSectionTesting(62, 67, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 16), new PddSectionTesting(65, 67, true))

                )),
                new PddSectionTimelineItem(PDD_SECTION_11, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(49, 53, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 14), new PddSectionTesting(48, 51, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 17), new PddSectionTesting(53, 53, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_12, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(51, 64, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 9), new PddSectionTesting(56, 64, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 14), new PddSectionTesting(59, 64, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 16), new PddSectionTesting(63, 64, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 17), new PddSectionTesting(63, 64, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_13, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(10, 14, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 9), new PddSectionTesting(14, 14, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(13, 14, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(14, 14, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 16), new PddSectionTesting(14, 14, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_14, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(70, 78, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 16), new PddSectionTesting(75, 78, true))

                )),
                new PddSectionTimelineItem(PDD_SECTION_15, Arrays.asList(
                )),
                new PddSectionTimelineItem(PDD_SECTION_16, Arrays.asList(

                )),
                new PddSectionTimelineItem(PDD_SECTION_17, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 12), new PddSectionTesting(8, 9, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(11, 12, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(8, 9, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 16), new PddSectionTesting(9, 9, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 17), new PddSectionTesting(8, 9, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 23), new PddSectionTesting(9, 9, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_18, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(28, 29, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 14), new PddSectionTesting(29, 29, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(29, 29, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 22), new PddSectionTesting(29, 29, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_19, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 7), new PddSectionTesting(19, 20, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 11), new PddSectionTesting(15, 15, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(15, 15, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 17), new PddSectionTesting(14, 15, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 19), new PddSectionTesting(15, 15, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_20, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(31, 32, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 11), new PddSectionTesting(30, 32, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(31, 32, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 23), new PddSectionTesting(32, 32, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_21, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(11, 13, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 14), new PddSectionTesting(10, 11, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(11, 11, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 16), new PddSectionTesting(10, 11, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 17), new PddSectionTesting(11, 11, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 20), new PddSectionTesting(11, 11, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_22, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(12, 14, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 9), new PddSectionTesting(13, 14, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(12, 14, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 14), new PddSectionTesting(14, 14, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 16), new PddSectionTesting(14, 14, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 30), new PddSectionTesting(14, 14, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_23, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 7), new PddSectionTesting(39, 47, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(33, 34, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(33, 34, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(34, 34, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 22), new PddSectionTesting(34, 34, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_24, Arrays.asList(

                )),
                new PddSectionTimelineItem(PDD_SECTION_25, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(7, 8, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 9), new PddSectionTesting(8, 8, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(8, 8, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(8, 8, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 16), new PddSectionTesting(8, 8, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 17), new PddSectionTesting(8, 8, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 19), new PddSectionTesting(8, 8, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_26, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 8), new PddSectionTesting(15, 18, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 9), new PddSectionTesting(18, 18, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 13), new PddSectionTesting(18, 18, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(18, 18, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 17), new PddSectionTesting(18, 18, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 23), new PddSectionTesting(18, 18, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_27, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 19), new PddSectionTesting(11, 12, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 20), new PddSectionTesting(11, 12, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 21), new PddSectionTesting(12, 12, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_28, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 19), new PddSectionTesting(9, 12, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 20), new PddSectionTesting(12, 12, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 21), new PddSectionTesting(12, 12, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_29, Arrays.asList(

                )),
                new PddSectionTimelineItem(PDD_SECTION_30, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 9), new PddSectionTesting(13, 13, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 10), new PddSectionTesting(20, 24, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 11), new PddSectionTesting(34, 35, true)),
                        new TimelineItem(LocalDate.of(2017, 3, 15), new PddSectionTesting(24, 24, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_31, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 20), new PddSectionTesting(56, 71, false))
                )),
                new PddSectionTimelineItem(PDD_SECTION_32, Arrays.asList(

                )),
                new PddSectionTimelineItem(PDD_SECTION_33, Arrays.asList(
                        new TimelineItem(LocalDate.of(2017, 3, 20), new PddSectionTesting(314, 368, false)),
                        new TimelineItem(LocalDate.of(2017, 3, 22), new PddSectionTesting(275, 300, true))
                )),
                new PddSectionTimelineItem(PDD_SECTION_34, Arrays.asList(

                ))
        );
    }

    private void createPddSections() {
        pddSectionRepository.save(PDD_SECTION_01);
        pddSectionRepository.save(PDD_SECTION_02);
        pddSectionRepository.save(PDD_SECTION_03);
        pddSectionRepository.save(PDD_SECTION_04);
        pddSectionRepository.save(PDD_SECTION_05);
        pddSectionRepository.save(PDD_SECTION_06);
        pddSectionRepository.save(PDD_SECTION_07);
        pddSectionRepository.save(PDD_SECTION_08);
        pddSectionRepository.save(PDD_SECTION_09);
        pddSectionRepository.save(PDD_SECTION_10);
        pddSectionRepository.save(PDD_SECTION_11);
        pddSectionRepository.save(PDD_SECTION_12);
        pddSectionRepository.save(PDD_SECTION_13);
        pddSectionRepository.save(PDD_SECTION_14);
        pddSectionRepository.save(PDD_SECTION_15);
        pddSectionRepository.save(PDD_SECTION_16);
        pddSectionRepository.save(PDD_SECTION_17);
        pddSectionRepository.save(PDD_SECTION_18);
        pddSectionRepository.save(PDD_SECTION_19);
        pddSectionRepository.save(PDD_SECTION_20);
        pddSectionRepository.save(PDD_SECTION_21);
        pddSectionRepository.save(PDD_SECTION_22);
        pddSectionRepository.save(PDD_SECTION_23);
        pddSectionRepository.save(PDD_SECTION_24);
        pddSectionRepository.save(PDD_SECTION_25);
        pddSectionRepository.save(PDD_SECTION_26);
        pddSectionRepository.save(PDD_SECTION_27);
        pddSectionRepository.save(PDD_SECTION_28);
        pddSectionRepository.save(PDD_SECTION_29);
        pddSectionRepository.save(PDD_SECTION_30);
        pddSectionRepository.save(PDD_SECTION_31);
        pddSectionRepository.save(PDD_SECTION_32);
        pddSectionRepository.save(PDD_SECTION_33);
        pddSectionRepository.save(PDD_SECTION_34);
    }
}
