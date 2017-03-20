package com.pdd.track.model.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SchoolDrivingEvent extends AbstractDrivingEvent {

    public SchoolDrivingEvent(final int duration) {
        super(duration);
    }
}
