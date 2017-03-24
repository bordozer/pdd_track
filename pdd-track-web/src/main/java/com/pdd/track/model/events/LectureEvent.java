package com.pdd.track.model.events;

import com.pdd.track.model.TimeLineItemEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LectureEvent extends TimelineEvent {

    private String pddSectionKey;

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.LECTURE;
    }
}
