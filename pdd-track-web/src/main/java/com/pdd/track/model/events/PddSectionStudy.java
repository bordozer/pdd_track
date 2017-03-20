package com.pdd.track.model.events;

import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;

@Data
public class PddSectionStudy extends PddSectionTimelineEvent {

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.STUDY;
    }
}
