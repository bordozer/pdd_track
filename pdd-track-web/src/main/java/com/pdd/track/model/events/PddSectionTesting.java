package com.pdd.track.model.events;

import com.pdd.track.model.Testing;
import com.pdd.track.model.TimeLineItemEventType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PddSectionTesting extends TimelineEvent {

    private Testing testing;

    public PddSectionTesting(final int passedQuestions, final int totalQuestions, final boolean passed) {
        this.testing = new Testing(passedQuestions, totalQuestions, passed);
    }

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.TESTING;
    }
}
