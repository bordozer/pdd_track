package com.pdd.track.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "pdd.track.collection")
public class LogEntity {
    @Id
    private String _id;
    private String field;
    private String value;
    private LocalDateTime time;
}
