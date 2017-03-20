package com.pdd.track.model.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdditionalDrivingEvent extends AbstractDrivingEvent {

    public AdditionalDrivingEvent(final int duration) {
        super(duration);
    }
}
