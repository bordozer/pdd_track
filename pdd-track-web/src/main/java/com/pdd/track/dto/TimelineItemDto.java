package com.pdd.track.dto;

import lombok.Data;

import java.util.List;

@Data
public class TimelineItemDto {
    private PddSectionDto pddSection;
    private List<TimelineDayDto> cells;
}
