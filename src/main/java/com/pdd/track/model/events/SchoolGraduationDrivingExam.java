package com.pdd.track.model.events;

import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;

@Data
public class SchoolGraduationDrivingExam extends TimelineEvent {

    private boolean passed;

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.SCHOOL_DRIVING_EXAM;
    }
}
