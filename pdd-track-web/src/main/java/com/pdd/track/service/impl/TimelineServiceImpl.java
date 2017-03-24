package com.pdd.track.service.impl;

import com.pdd.track.converter.TimelineConverter;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.model.Timeline;
import com.pdd.track.model.TimelineStudy;
import com.pdd.track.repository.TimelineRepository;
import com.pdd.track.repository.TimelineStudyRepository;
import com.pdd.track.service.TimelineService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;

@Service
public class TimelineServiceImpl implements TimelineService {

    @Inject
    private TimelineRepository timelineRepository;

    @Inject
    private TimelineStudyRepository timelineStudyRepository;

    @Override
    public TimelineDto getTimeline(final String studentKey, final String rulesSetKey, final LocalDate onDate) {
        Timeline timeline = timelineRepository.findOneByStudentKey(studentKey);
        TimelineStudy timelineStudy = timelineStudyRepository.findOneBy_id(rulesSetKey);
        return TimelineConverter.toDto(timeline, timelineStudy, onDate);
    }

    @Override
    public Timeline create(final Timeline entity) {
        return timelineRepository.save(entity);
    }

}
