package com.pdd.track.repository;

import com.pdd.track.model.TimelineStudy;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimelineStudyRepository extends MongoRepository<TimelineStudy, String> {

    TimelineStudy findOneBy_id(final String _id);
}
