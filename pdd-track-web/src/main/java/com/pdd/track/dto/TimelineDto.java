package com.pdd.track.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TimelineDto {
    private List<TimelineDayColumn> dayColumns;
    private List<TimelineItemDto> items;
    private LocalDate startDate;
    private LocalDate endDate;
}
