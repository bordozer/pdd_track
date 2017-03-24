package com.pdd.track.controllers.rest;

import com.pdd.track.dto.TimelineDto;
import com.pdd.track.service.DataGenerationService;
import com.pdd.track.service.TimelineService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.LocalDate;

@RestController
@RequestMapping(value = "/student/timeline")
public class StudentTimelineController {

    @Inject
    private TimelineService timelineService;

    @Inject
    private DataGenerationService dataGenerationService;

    //http://localhost:8084/student/timeline/qaz-wsx-edc/rules-set/1/
    @RequestMapping(method = RequestMethod.GET, value = "/{studentKey}/rules-set/{rulesSetKey}/")
    public TimelineDto getStudentTimeline(@PathVariable("studentKey") final String studentKey, @PathVariable("rulesSetKey") final String rulesSetKey) {
        return timelineService.getTimeline(studentKey, rulesSetKey, LocalDate.now());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data-create/")
    public boolean createLogRecord() {
        dataGenerationService.createData();
        return true;
    }
}
