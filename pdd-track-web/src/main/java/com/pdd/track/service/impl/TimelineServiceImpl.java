package com.pdd.track.service.impl;

import com.pdd.track.converter.TimelineConverter;
import com.pdd.track.dto.TimelineDto;
import com.pdd.track.entity.TimelineEntity;
import com.pdd.track.repository.TimelineRepository;
import com.pdd.track.service.TimelineService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;

@Service
public class TimelineServiceImpl implements TimelineService {

    @Inject
    private TimelineRepository timelineRepository;

    @Override
    public TimelineDto getForStudent(final String studentKey, final LocalDate onDate) {
        return TimelineConverter.toDto(timelineRepository.findOneByStudentKey(studentKey), onDate);
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
