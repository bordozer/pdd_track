package com.pdd.track.model.events;

import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LectureStudyEvent extends AbstractLectureEvent {

    public LectureStudyEvent(final String pddSectionKey) {
        super(pddSectionKey);
    }

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.LECTURE_STUDY;
    }
}
