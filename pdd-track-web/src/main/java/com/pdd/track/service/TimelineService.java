package com.pdd.track.service;

import com.pdd.track.dto.TimelineDto;
import com.pdd.track.entity.TimelineEntity;

import java.time.LocalDate;

public interface TimelineService {

    TimelineDto getForStudent(String studentKey, final LocalDate onDate);

    TimelineEntity create(TimelineEntity entity);

    void deleteAll();
}
