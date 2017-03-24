package com.pdd.track.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "PDD.TRACK.TIMELINE_STUDY")
public class TimelineStudy {
    private String _id;
    private String timelineId;
    private List<PddSectionTimelineItem> pddSectionTimelineItems;
}