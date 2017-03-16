package com.pdd.track.model;

import com.pdd.track.model.events.TimelineEvent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TimelineItem {
    private LocalDate date;
    private TimelineEvent event;
}
