package com.pdd.track.controllers;

import com.google.common.collect.Lists;
import com.pdd.track.entity.UserStudyTimelineEntity;
import com.pdd.track.model.Gender;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.PddSectionTimelineItem;
import com.pdd.track.model.SchoolTimelineItem;
import com.pdd.track.model.Student;
import com.pdd.track.model.TimelineItem;
import com.pdd.track.model.events.DrivingEvent;
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

    private UserStudyTimelineEntity constructUserStudyTimelineEntity() {
        UserStudyTimelineEntity entity = new UserStudyTimelineEntity();
        entity.setStudent(STUDENT);
        entity.setPddSectionTimelineItems(constructPddSectionTimelineItems());
        entity.setSchoolTimelineItems(constructTimelineItems());
        return entity;
    }

    private List<SchoolTimelineItem> constructTimelineItems() {
        TimelineItem driving1 = new TimelineItem(LocalDate.of(2017, 3, 17), new DrivingEvent(50));
        return Lists.newArrayList(new SchoolTimelineItem(driving1));
    }

    private List<PddSectionTimelineItem> constructPddSectionTimelineItems() {
        PddSection pddSection = new PddSection();
        pddSection.setKey("qwe-asd-zxc");
        pddSection.setNumber("1");
        pddSection.setName("General");
        pddSection.setQuestionsCount(116);

        TimelineItem lecture1 = new TimelineItem(LocalDate.of(2017, 3, 4), new LectureEvent());
        TimelineItem lecture2 = new TimelineItem(LocalDate.of(2017, 3, 5), new LectureEvent());
        TimelineItem study1 = new TimelineItem(LocalDate.of(2017, 3, 6), new PddSectionStudy());
        TimelineItem testing1 = new TimelineItem(LocalDate.of(2017, 3, 7), new PddSectionTesting(17, 20, false));

        PddSectionTimelineItem sectionTimelineItem = new PddSectionTimelineItem();
        sectionTimelineItem.setPddSection(pddSection);
        sectionTimelineItem.setTimelineItems(Lists.newArrayList(lecture1, lecture2, study1, testing1));

        return Lists.newArrayList(sectionTimelineItem);
    }
}
