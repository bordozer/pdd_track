package com.pdd.track.repository;

import com.pdd.track.entity.DEL_LogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogRepository extends MongoRepository<DEL_LogEntity, String> {

}
