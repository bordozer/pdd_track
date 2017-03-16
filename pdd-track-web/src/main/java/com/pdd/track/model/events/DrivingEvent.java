package com.pdd.track.model.events;

import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;

@Data
public class DrivingEvent extends TimelineEvent {
    private int duration; // minutes

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.DRIVING;
    }
}
