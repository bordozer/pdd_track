package com.pdd.track.model.events;

import com.pdd.track.model.Testing;
import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;

@Data
public class GraduationTesting extends TimelineEvent {

    private Testing testing;

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.GRADUATION_THEORY_EXAM;
    }
}
