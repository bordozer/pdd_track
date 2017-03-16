package com.pdd.track.service;

import com.pdd.track.entity.UserStudyTimelineEntity;

import java.util.List;

public interface TimelineService {

    List<UserStudyTimelineEntity> getAll(String userKey);

    UserStudyTimelineEntity create(UserStudyTimelineEntity entity);
}
