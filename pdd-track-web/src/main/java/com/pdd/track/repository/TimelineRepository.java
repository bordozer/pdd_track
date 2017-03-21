package com.pdd.track.repository;

import com.pdd.track.entity.UserStudyTimelineEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TimelineRepository extends MongoRepository<UserStudyTimelineEntity, String> {

    List<UserStudyTimelineEntity> findAllByStudentKey(final String userKey);
}
