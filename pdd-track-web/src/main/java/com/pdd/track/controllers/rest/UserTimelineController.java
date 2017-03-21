package com.pdd.track.controllers.rest;

import com.pdd.track.converter.TimelineConverter;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.entity.UserStudyTimelineEntity;
import com.pdd.track.service.DataGenerationService;
import com.pdd.track.service.TimelineService;
import com.pdd.track.service.impl.DataGenerationServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/timeline")
public class UserTimelineController {

    @Inject
    private TimelineService timelineService;

    @Inject
    private DataGenerationService dataGenerationService;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public List<TimelineDto> getAll() {
        return timelineService.getAll(DataGenerationServiceImpl.STUDENT.getKey()).stream()
                .map(TimelineConverter::toDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/data-create/")
    public UserStudyTimelineEntity createLogRecord() {
        return dataGenerationService.createData();
    }
}
