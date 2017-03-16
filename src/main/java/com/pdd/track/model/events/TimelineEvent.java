package com.pdd.track.model.events;

import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;

@Data
public abstract class TimelineEvent {
    public abstract TimeLineItemEventType getEventType();
}
