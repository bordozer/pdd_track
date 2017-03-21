package com.pdd.track.controllers.rest;

import com.pdd.track.converter.TimelineConverter;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.entity.UserStudyTimelineEntity;
import com.pdd.track.service.DataGenerationService;
import com.pdd.track.service.TimelineService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/student/timeline")
public class StudentTimelineController {

    @Inject
    private TimelineService timelineService;

    @Inject
    private DataGenerationService dataGenerationService;

    @RequestMapping(method = RequestMethod.GET, value = "/{userKey}/")
    public List<TimelineDto> getStudentTimeline(@PathVariable("userKey") final String userKey) {
        return timelineService.getAll(userKey).stream()
                .map(TimelineConverter::toDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data-create/")
    public UserStudyTimelineEntity createLogRecord() {
        return dataGenerationService.createData();
    }
}
