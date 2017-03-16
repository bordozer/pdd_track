package com.pdd.track.model;

import lombok.Data;

import java.util.List;

@Data
public class PddSectionTimelineItem {
    private PddSection pddSection;
    private List<TimelineItem> timelineItems;
}
