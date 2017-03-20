package com.pdd.track.model;

import com.pdd.track.utils.RandomUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PddSection {
    private String key;
    private String number;
    private String name;
    private int questionsCount;

    public PddSection(final String key, final String number, final String name, final int questionsCount) {
        this.key = key;
        this.number = number;
        this.name = name;
        this.questionsCount = questionsCount;
    }

    public PddSection(final String number, final String name, final int questionsCount) {
        this.key = RandomUtils.UUID();
        this.number = number;
        this.name = name;
        this.questionsCount = questionsCount;
    }
}
