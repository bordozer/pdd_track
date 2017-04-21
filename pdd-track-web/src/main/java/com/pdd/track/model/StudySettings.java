package com.pdd.track.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudySettings {
    private int sectionTooLongWithoutTestingAfterStudyDays;
    private int sectionTooLongWithoutRetestingDays;
    private int minTestCount;
}
