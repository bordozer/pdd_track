package com.pdd.track.service.impl;

import com.pdd.track.converter.TimelineConverter;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.SchoolTimeline;
import com.pdd.track.model.StudentTimeline;
import com.pdd.track.repository.PddSectionRepository;
import com.pdd.track.repository.TimelineRepository;
import com.pdd.track.repository.TimelineStudyRepository;
import com.pdd.track.service.TimelineService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

@Service
public class TimelineServiceImpl implements TimelineService {

    @Inject
    private TimelineRepository timelineRepository;

    @Inject
    private TimelineStudyRepository timelineStudyRepository;

    @Inject
    private PddSectionRepository pddSectionRepository;

    @Override
    public TimelineDto getTimeline(final String studentKey, final String rulesSetKey, final LocalDate onDate) {
        List<PddSection> pddSections = pddSectionRepository.findAll();
        SchoolTimeline schoolTimeline = timelineRepository.findOneByStudentKey(studentKey);
        StudentTimeline studentTimeline = timelineStudyRepository.findOneBy_id(rulesSetKey);
        return TimelineConverter.toDto(pddSections, schoolTimeline, studentTimeline, onDate);
    }

    @Override
    public SchoolTimeline create(final SchoolTimeline entity) {
        return timelineRepository.save(entity);
    }

}
