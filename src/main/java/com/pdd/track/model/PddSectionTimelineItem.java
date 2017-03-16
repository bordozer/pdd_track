package com.pdd.track.model;

import com.pdd.track.entity.rule.PddSectionEntity;
import lombok.Data;

import java.util.List;

@Data
public class PddSectionTimelineItem {
    private String _id;
    private PddSectionEntity pddSection;
    private List<TimelineItem> timelineItems;
}
