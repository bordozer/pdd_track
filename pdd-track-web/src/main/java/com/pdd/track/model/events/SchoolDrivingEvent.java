package com.pdd.track.model.events;

import com.pdd.track.model.Car;
import com.pdd.track.model.Instructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SchoolDrivingEvent extends AbstractDrivingEvent {

    public SchoolDrivingEvent(final Instructor instructor, final Car car, final int duration) {
        super(instructor, car, duration);
    }
}
