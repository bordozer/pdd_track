package com.pdd.track.entity.rule;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "PDD.TRACK.RULES_SET")
public class RulesSet {
    @Id
    private String _id;
    private String name;
    private int year;
}
