package com.pdd.track.model.events;

import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LectureEvent extends AbstractLectureEvent {

    public LectureEvent(final String pddSectionKey) {
        super(pddSectionKey);
    }

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.LECTURE;
    }
}
