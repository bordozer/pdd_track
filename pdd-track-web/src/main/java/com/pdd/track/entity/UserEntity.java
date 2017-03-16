package com.pdd.track.entity;

import com.pdd.track.model.Gender;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "PDD.TRACK.USERS")
public class UserEntity {
    @Id
    private String _id;
    private String name;
    private Gender gender;
}
