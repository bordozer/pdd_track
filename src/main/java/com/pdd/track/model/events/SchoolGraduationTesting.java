package com.pdd.track.model.events;

import com.pdd.track.model.Testing;
import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;

@Data
public class SchoolGraduationTesting extends TimelineEvent {

    private Testing testing;

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.SCHOOL_THEORY_EXAM;
    }
}
