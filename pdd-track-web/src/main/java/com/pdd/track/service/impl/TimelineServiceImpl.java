package com.pdd.track.service.impl;

import com.pdd.track.entity.TimelineEntity;
import com.pdd.track.repository.TimelineRepository;
import com.pdd.track.service.TimelineService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class TimelineServiceImpl implements TimelineService {

    @Inject
    private TimelineRepository timelineRepository;

    @Override
    public TimelineEntity getForStudent(final String studentKey) {
        return timelineRepository.findOneByStudentKey(studentKey);
    }

    @Override
    public TimelineEntity create(final TimelineEntity entity) {
        return timelineRepository.save(entity);
    }

    @Override
    public void deleteAll() {
        timelineRepository.deleteAll();
    }
}
