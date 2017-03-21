package com.pdd.track.dto;

import lombok.Data;

@Data
public class PddSectionDto {
    private String key;
    private String number; // section number in PDD
    private String name;
    private int questionsCount;

}
