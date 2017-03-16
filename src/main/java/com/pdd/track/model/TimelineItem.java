package com.pdd.track.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimelineItem {
    private LocalDateTime date;
    private TimelineEvent events;
}
