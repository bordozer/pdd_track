package com.pdd.track.model.events;

import com.pdd.track.model.Car;
import com.pdd.track.model.Instructor;
import com.pdd.track.model.TimeLineItemEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractDrivingEvent extends TimelineEvent {

    private Instructor instructor;
    private Car car;
    private int duration; // minutes

    @Override
    public TimeLineItemEventType getEventType() {
        return TimeLineItemEventType.DRIVING;
    }
}
