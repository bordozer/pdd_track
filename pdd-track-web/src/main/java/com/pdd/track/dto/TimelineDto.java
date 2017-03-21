package com.pdd.track.dto;

import lombok.Data;

import java.util.List;

@Data
public class TimelineDto {
    private List<TimelineDayColumn> dayColumns;
    private List<TimelineItemDto> items;
}
