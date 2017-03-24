package com.pdd.track.model.events;

import com.pdd.track.model.TimeLineItemEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@NoArgsConstructor TODO
@AllArgsConstructor
public class PddSectionStudy extends TimelineEvent {

    private String pddSectionKey;

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.STUDY;
    }
}
