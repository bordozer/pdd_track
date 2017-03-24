package com.pdd.track.repository;

import com.pdd.track.model.StudentTimeline;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimelineStudyRepository extends MongoRepository<StudentTimeline, String> {

    StudentTimeline findOneBy_id(final String _id);
}
