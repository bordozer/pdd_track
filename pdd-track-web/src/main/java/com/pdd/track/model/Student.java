package com.pdd.track.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Student {
    private String key;
    private String name;
    private Gender gender;
}
