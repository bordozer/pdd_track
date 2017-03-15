package com.pdd.track.repository;

import com.pdd.track.entity.LogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogRepository extends MongoRepository<LogEntity, String> {

}
