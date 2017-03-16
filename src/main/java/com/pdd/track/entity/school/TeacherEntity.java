package com.pdd.track.entity.school;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "PDD.TRACK.TEACHERS")
public class TeacherEntity {
    private String _id;
    private String name;
}
