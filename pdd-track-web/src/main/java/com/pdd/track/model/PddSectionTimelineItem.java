package com.pdd.track.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PddSectionTimelineItem {
    private PddSection pddSection;
    private List<TimelineItem> timelineItems;
}
