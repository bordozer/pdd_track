package com.pdd.track.service;

import com.pdd.track.dto.TimelineDto;
import com.pdd.track.model.Timeline;

import java.time.LocalDate;

public interface TimelineService {

    TimelineDto getTimeline(String studentKey, final String rulesSetKey, final LocalDate onDate);

    Timeline create(Timeline entity);
}
