package com.pdd.track.controllers.rest;

import com.pdd.track.converter.TimelineConverter;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.entity.TimelineEntity;
import com.pdd.track.service.DataGenerationService;
import com.pdd.track.service.TimelineService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping(value = "/student/timeline")
public class StudentTimelineController {

    @Inject
    private TimelineService timelineService;

    @Inject
    private DataGenerationService dataGenerationService;

    @RequestMapping(method = RequestMethod.GET, value = "/{studentKey}/")
    public TimelineDto getStudentTimeline(@PathVariable("studentKey") final String studentKey) {
        return TimelineConverter.toDto(timelineService.getForStudent(studentKey));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data-create/")
    public TimelineEntity createLogRecord() {
        return dataGenerationService.createData();
    }
}
