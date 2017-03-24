package com.pdd.track.repository;

import com.pdd.track.model.PddSectionTimeline;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimelineStudyRepository extends MongoRepository<PddSectionTimeline, String> {

    PddSectionTimeline findOneBy_id(final String _id);
}
