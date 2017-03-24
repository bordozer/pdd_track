package com.pdd.track.repository;

import com.pdd.track.model.Timeline;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimelineRepository extends MongoRepository<Timeline, String> {

    Timeline findOneByStudentKey(final String userKey);
}
