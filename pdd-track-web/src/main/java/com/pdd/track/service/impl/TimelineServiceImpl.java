package com.pdd.track.service.impl;

import com.pdd.track.converter.TimelineConverter;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.SchoolTimeline;
import com.pdd.track.model.PddSectionTimeline;
import com.pdd.track.model.StudySettings;
import com.pdd.track.repository.PddSectionRepository;
import com.pdd.track.repository.SchoolTimelineRepository;
import com.pdd.track.repository.PddSectionTimelineRepository;
import com.pdd.track.service.TimelineService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

@Service
public class TimelineServiceImpl implements TimelineService {

    @Inject
    private SchoolTimelineRepository schoolTimelineRepository;

    @Inject
    private PddSectionTimelineRepository pddSectionTimelineRepository;

    @Inject
    private PddSectionRepository pddSectionRepository;

    @Override
    public TimelineDto getTimeline(final String studentKey, final String rulesSetKey, final LocalDate onDate) {
        List<PddSection> pddSections = pddSectionRepository.findAll();
        SchoolTimeline schoolTimeline = schoolTimelineRepository.findOneByStudentKey(studentKey);
        PddSectionTimeline pddSectionTimeline = pddSectionTimelineRepository.findOneBy_id(rulesSetKey);
        StudySettings settings = loadStudySettings(studentKey, rulesSetKey);
        return TimelineConverter.toDto(pddSections, schoolTimeline, pddSectionTimeline, settings, onDate);
    }

    @Override
    public SchoolTimeline create(final SchoolTimeline entity) {
        return schoolTimelineRepository.save(entity);
    }

    private StudySettings loadStudySettings(final String studentKey, final String rulesSetKey) {
        if (rulesSetKey.equals(DataGenerationServiceImpl.KHARKOV_2017_RULE_KEY_ID)) {
            return StudySettings.builder()
                    .minTestCount(1)
                    .sectionTooLongWithoutTestingAfterStudyDays(30)
                    .sectionTooLongWithoutRetestingDays(30)
                    .build();
        }
        return StudySettings.builder()
                .minTestCount(3)
                .sectionTooLongWithoutTestingAfterStudyDays(2)
                .sectionTooLongWithoutRetestingDays(5)
                .build();
    }
}
