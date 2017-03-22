package com.pdd.track.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestingDto {
    private int passedQuestions;
    private int totalQuestions;
    private boolean passed;
    private String percentage;
}
