package com.pdd.track.model.events;

import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;

@Data
public class LectureEvent extends PddSectionTimelineEvent {

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.LECTURE;
    }
}
