package com.pdd.track.controllers;

import com.pdd.track.entity.UserStudyTimelineEntity;
import com.pdd.track.model.Gender;
import com.pdd.track.model.Student;
import com.pdd.track.model.Student;
import com.pdd.track.service.TimelineService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
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
        UserStudyTimelineEntity entity = new UserStudyTimelineEntity();
        entity.setStudent(STUDENT);

        return timelineService.create(entity);
    }
}
