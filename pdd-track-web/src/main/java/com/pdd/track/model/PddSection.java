package com.pdd.track.model;

import com.pdd.track.utils.RandomUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "PDD.TRACK.PDD_SECTION")
public class PddSection {
    private String key;
    private String number;
    private String name;
    private int questionsCount;

    public PddSection(final String number, final String name, final int questionsCount) {
        this.key = RandomUtils.UUID();
        this.number = number;
        this.name = name;
        this.questionsCount = questionsCount;
    }
}
