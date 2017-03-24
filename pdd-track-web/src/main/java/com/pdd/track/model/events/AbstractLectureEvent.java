package com.pdd.track.model.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class AbstractLectureEvent extends TimelineEvent {
    private String pddSectionKey;

    public AbstractLectureEvent(final String pddSectionKey) {
        this.pddSectionKey = pddSectionKey;
    }
}
