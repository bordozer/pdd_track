package com.pdd.track.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrivingDto {
    private CarDto car;
    private InstructorDto instructor;
    private int duration; // minutes

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarDto {
        private String model;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstructorDto {
        private String name;
    }
}
