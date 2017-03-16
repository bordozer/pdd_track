package com.pdd.track.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Testing {
    private int passedQuestions;
    private int totalQuestions;
    private boolean passed;
}
