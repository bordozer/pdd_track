package com.pdd.track.entity.rule;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "PDD.TRACK.SECTIONS")
public class PddSectionEntity {
    @Id
    private String _id;
    private RulesSet rulesSet;
    private String name;
    private int questionsCount;
}
