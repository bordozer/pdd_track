package com.pdd.track.service.impl;

import com.pdd.track.entity.UserStudyTimelineEntity;
import com.pdd.track.repository.TimelineRepository;
import com.pdd.track.service.TimelineService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class TimelineServiceImpl implements TimelineService {

    @Inject
    private TimelineRepository timelineRepository;

    @Override
    public List<UserStudyTimelineEntity> getAll(final String userKey) {
        return timelineRepository.findAll();
    }

    @Override
    public UserStudyTimelineEntity create(final UserStudyTimelineEntity entity) {
        return timelineRepository.save(entity);
    }

    @Override
    public void deleteAll() {
        timelineRepository.deleteAll();
    }
}
