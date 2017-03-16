package com.pdd.track.controllers;

import com.google.common.collect.Lists;
import com.pdd.track.entity.UserStudyTimelineEntity;
import com.pdd.track.model.Gender;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.PddSectionTimelineItem;
import com.pdd.track.model.Student;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.events.LectureEvent;
import com.pdd.track.model.events.PddSectionStudy;
import com.pdd.track.model.events.PddSectionTesting;
import com.pdd.track.service.TimelineService;
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

    @Inject
    private TimelineService timelineService;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public List<UserStudyTimelineEntity> getAll() {
        return timelineService.getAll(STUDENT.getKey());
    }

    // TODO: for dev purposes only
    @RequestMapping(method = RequestMethod.PUT, value = "/add/")
    public UserStudyTimelineEntity createLogRecord() {
        return timelineService.create(constructUserStudyTimelineEntity());
    }

    private List<TimelineItem> constructTimelineItems() {
        TimelineItem lecture1 = new TimelineItem();
        lecture1.setDate(LocalDate.of(2017, 3, 4));
        lecture1.setEvent(new LectureEvent());

        TimelineItem lecture2 = new TimelineItem();
        lecture1.setDate(LocalDate.of(2017, 3, 5));
        lecture1.setEvent(new LectureEvent());

        TimelineItem study1 = new TimelineItem();
        lecture1.setDate(LocalDate.of(2017, 3, 6));
        lecture1.setEvent(new PddSectionStudy());

        TimelineItem testing1 = new TimelineItem();
        lecture1.setDate(LocalDate.of(2017, 3, 6));
        lecture1.setEvent(new PddSectionTesting(17, 20, false));

        return Lists.newArrayList(lecture1, lecture2, study1, testing1);
    }

    private List<PddSectionTimelineItem> constructPddSectionTimelineItems() {
        PddSection pddSection = new PddSection();
        pddSection.setKey("qwe-asd-zxc");
        pddSection.setName("General");
        pddSection.setQuestionsCount(116);

        TimelineItem lecture1 = new TimelineItem();
        lecture1.setDate(LocalDate.of(2017, 3, 4));
        lecture1.setEvent(new LectureEvent());

        TimelineItem lecture2 = new TimelineItem();
        lecture1.setDate(LocalDate.of(2017, 3, 5));
        lecture1.setEvent(new LectureEvent());

        TimelineItem study1 = new TimelineItem();
        lecture1.setDate(LocalDate.of(2017, 3, 6));
        lecture1.setEvent(new PddSectionStudy());

        TimelineItem testing1 = new TimelineItem();
        lecture1.setDate(LocalDate.of(2017, 3, 6));
        lecture1.setEvent(new PddSectionTesting(17, 20, false));

        PddSectionTimelineItem sectionTimelineItem = new PddSectionTimelineItem();
        sectionTimelineItem.setPddSection(pddSection);
        sectionTimelineItem.setTimelineItems(Lists.newArrayList(lecture1, lecture2, study1, testing1));

        return Lists.newArrayList(sectionTimelineItem);
    }

    private UserStudyTimelineEntity constructUserStudyTimelineEntity() {
        UserStudyTimelineEntity entity = new UserStudyTimelineEntity();
        entity.setStudent(STUDENT);
        entity.setPddSectionTimelineItems(constructPddSectionTimelineItems());
        entity.setTimelineItems(constructTimelineItems());
        return entity;
    }
}
