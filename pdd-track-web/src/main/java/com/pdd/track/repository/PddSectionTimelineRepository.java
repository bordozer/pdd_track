package com.pdd.track.repository;

import com.pdd.track.model.PddSectionTimeline;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PddSectionTimelineRepository extends MongoRepository<PddSectionTimeline, String> {

    PddSectionTimeline findOneBy_id(final String _id);
}
