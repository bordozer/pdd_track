package com.pdd.track.repository;

import com.pdd.track.model.SchoolTimeline;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimelineRepository extends MongoRepository<SchoolTimeline, String> {

    SchoolTimeline findOneByStudentKey(final String userKey);
}
