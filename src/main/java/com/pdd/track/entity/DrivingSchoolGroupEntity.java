package com.pdd.track.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "PDD.TRACK.DRIVING_SCHOOL_GROUP")
public class DrivingSchoolGroupEntity {
    @Id
    private String _id;
    private String drivingSchoolId;
    private String name;
    private String groupOwnerId; // TODO: UserEntity?

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<DayOfWeek> lectureDaysOfWeek;

    private String teacherId; // TODO: TeacherEntity?
    private String teacherName;
}
