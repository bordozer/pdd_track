package com.pdd.track.controllers;

import com.pdd.track.entity.UserStudyTimelineEntity;
import com.pdd.track.service.DataGenerationService;
import com.pdd.track.service.TimelineService;
import com.pdd.track.service.impl.DataGenerationServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping(value = "/timeline")
public class UserTimelineController {

    @Inject
    private TimelineService timelineService;

    @Inject
    private DataGenerationService dataGenerationService;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public List<UserStudyTimelineEntity> getAll() {
        return timelineService.getAll(DataGenerationServiceImpl.STUDENT.getKey());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data-create/")
    public UserStudyTimelineEntity createLogRecord() {
        return dataGenerationService.createData();
    }
}
