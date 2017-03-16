package com.pdd.track.model.events;

import com.pdd.track.model.Testing;
import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;

@Data
public class PddSectionTesting extends PddSectionTimelineEvent {

    private Testing testing;

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.TESTING;
    }
}
