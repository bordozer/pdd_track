package com.pdd.track.model.events;

import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;

@Data
public class LectureEvent extends TimelineEvent {

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.LECTURE;
    }
}
