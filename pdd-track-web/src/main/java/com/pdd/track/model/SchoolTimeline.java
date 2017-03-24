package com.pdd.track.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "PDD.TRACK.TIMELINE")
public class SchoolTimeline {
    private String _id;
    private Student student;
    private List<StudyingTimelineItem> studyingTimelineItems;
}
