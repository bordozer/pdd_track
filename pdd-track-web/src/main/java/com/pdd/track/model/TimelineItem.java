package com.pdd.track.model;

import com.pdd.track.model.events.TimelineEvent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TimelineItem {
    private final LocalDate date;
    private final TimelineEvent event;
}
