package com.pdd.track.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "PDD.TRACK.RULES")
public class RulesSetEntity {
    @Id
    private String _id;
    private String name;
    private int year;
}
