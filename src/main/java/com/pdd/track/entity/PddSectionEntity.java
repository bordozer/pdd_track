package com.pdd.track.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "PDD.TRACK.SECTIONS")
public class PddSectionEntity {
    @Id
    private String _id;
    private RulesSetEntity rulesSet;
    private String name;
    private int questionsCount;
}
