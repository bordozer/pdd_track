package com.pdd.track.converter;

import com.pdd.track.dto.DrivingDto.CarDto;
import com.pdd.track.dto.DrivingDto.InstructorDto;
import com.pdd.track.dto.PddSectionDto;
import com.pdd.track.dto.TestingDto;
import com.pdd.track.model.Car;
import com.pdd.track.model.Instructor;
import com.pdd.track.model.PddSection;
import com.pdd.track.model.PddSection.PddSectionQuestions;
import com.pdd.track.model.PddSection.RuleSetQuestions;
import com.pdd.track.model.Testing;
import com.pdd.track.utils.CommonUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static PddSectionDto convertPddSection(final PddSection pddSection, final String ruleSetKey) {
        PddSectionDto dto = new PddSectionDto();
        dto.setKey(pddSection.getKey());
        dto.setNumber(pddSection.getNumber());
        dto.setName(pddSection.getName());
        dto.setQuestionsCount(getQuestionsBySectionNumberCount(pddSection, ruleSetKey).getQuestionsCount());
        return dto;
    }

    public static Map<String, Integer> getQuestionsBySectionNumberCount(final List<PddSection> sections, final String ruleSetKey) {
        List<PddSectionQuestions> collect = sections.stream()
                .map(section -> new PddSectionQuestions(section.getNumber(), getQuestionsBySectionNumberCount(section, ruleSetKey).getQuestionsCount()))
                .collect(Collectors.toList());
        return collect.stream().collect(Collectors.toMap(PddSectionQuestions::getSectionNumber, PddSectionQuestions::getQuestionsCount));
    }

    private static RuleSetQuestions getQuestionsBySectionNumberCount(final PddSection section, final String ruleSetKey) {
        return section.getQuestionsByRules().stream().filter(o -> o.getRuleSetNumber().equals(ruleSetKey)).findAny().orElse(new RuleSetQuestions(section.getNumber(), 0));
    }
}
