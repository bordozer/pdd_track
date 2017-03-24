package com.pdd.track.repository;

import com.pdd.track.model.PddSection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PddSectionRepository extends MongoRepository<PddSection, String> {

}
