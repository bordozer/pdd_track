package com.pdd.track.entity;

import com.pdd.track.model.PddSectionTimelineItem;
import com.pdd.track.model.StudyingTimelineItem;
import com.pdd.track.model.Student;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "PDD.TRACK.STUDYING_TIMELINE")
public class TimelineEntity {
    private String _id;
    private Student student;
    private List<StudyingTimelineItem> studyingTimelineItems;
    private List<PddSectionTimelineItem> pddSectionTimelineItems;
}