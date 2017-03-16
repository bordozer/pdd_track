package com.pdd.track.controllers;

import com.pdd.track.entity.UserStudyTimelineEntity;
import com.pdd.track.service.TimelineService;
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

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public List<UserStudyTimelineEntity> getAll() {
        return timelineService.getAll("qaz-wsx-edc"); // TODO: read user from context
    }

    /*@RequestMapping(method = RequestMethod.PUT, value = "/log")
    public UserStudyTimelineEntity createLogRecord(@RequestBody final DEL_LogItemDto dto) {
        return new UserStudyTimelineEntity();
    }*/
}
