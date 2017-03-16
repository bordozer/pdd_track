package com.pdd.track.model;

import lombok.Data;

@Data
public abstract class TimelineEvent {
    private TimeLineItemEventType eventType;
}
