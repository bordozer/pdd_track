package com.pdd.track.repository;

import com.pdd.track.entity.UserStudyTimelineEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimelineRepository extends MongoRepository<UserStudyTimelineEntity, String> {

}
