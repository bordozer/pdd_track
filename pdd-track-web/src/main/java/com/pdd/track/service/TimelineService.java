package com.pdd.track.service;

import com.pdd.track.entity.TimelineEntity;

public interface TimelineService {

    TimelineEntity getForStudent(String studentKey);

    TimelineEntity create(TimelineEntity entity);

    void deleteAll();
}
