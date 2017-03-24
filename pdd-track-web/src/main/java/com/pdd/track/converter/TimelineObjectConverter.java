package com.pdd.track.converter;

import com.pdd.track.dto.DrivingDto.CarDto;
import com.pdd.track.dto.DrivingDto.InstructorDto;
import com.pdd.track.dto.PddSectionDto;
import com.pdd.track.dto.TestingDto;
import com.pdd.track.model.Car;
import com.pdd.track.model.Instructor;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.Testing;
import com.pdd.track.utils.CommonUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimelineObjectConverter {

    public static TestingDto convertTestingEvent(final Testing testing) {
        double percentage = CommonUtils.getPercentage(testing);
        String percentageFormatted = CommonUtils.formatDouble(percentage);
        return new TestingDto(testing.getPassedQuestions(), testing.getTotalQuestions(), testing.isPassed(), percentage, percentageFormatted);
    }

    public static CarDto convertCar(final Car car) {
        return new CarDto(car.getModel());
    }

    public static InstructorDto convertInstructor(final Instructor instructor) {
        return new InstructorDto(instructor.getName());
    }

    public static PddSectionDto convertPddSection(final PddSection pddSection) {
        PddSectionDto dto = new PddSectionDto();
        dto.setKey(pddSection.getKey());
        dto.setNumber(pddSection.getNumber());
        dto.setName(pddSection.getName());
        dto.setQuestionsCount(pddSection.getQuestionsCount());
        return dto;
    }
}
