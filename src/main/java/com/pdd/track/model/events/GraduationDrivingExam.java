package com.pdd.track.model.events;

import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;

@Data
public class GraduationDrivingExam extends TimelineEvent {

    private boolean passed;

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.GRADUATION_DRIVING_EXAM;
    }
}
