package com.pdd.track.repository;

import com.pdd.track.entity.TimelineEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimelineRepository extends MongoRepository<TimelineEntity, String> {

    TimelineEntity findOneByStudentKey(final String userKey);
}
