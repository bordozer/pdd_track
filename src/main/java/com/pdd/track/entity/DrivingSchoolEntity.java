package com.pdd.track.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "PDD.TRACK.DRIVING_SCHOOL")
public class DrivingSchoolEntity {
    @Id
    private String _id;
    private String name;
    private String address;
}
