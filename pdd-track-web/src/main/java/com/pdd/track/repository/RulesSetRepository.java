package com.pdd.track.repository;

import com.pdd.track.entity.rule.RulesSet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RulesSetRepository extends MongoRepository<RulesSet, String> {

    RulesSet findOneBy_id(final String _id);
}
