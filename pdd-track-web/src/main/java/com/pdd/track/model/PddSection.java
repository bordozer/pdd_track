package com.pdd.track.model;

import com.pdd.track.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "PDD.TRACK.PDD_SECTION")
public class PddSection {
    private String key;
    private String number;
    private String name;
    private List<PddSectionRuleSet> ruleSet;

    public PddSection(final String number, final String name) {
        this.key = CommonUtils.UUID();
        this.number = number;
        this.name = name;
    }

    @Data
    public static class PddSectionRuleSet {
        private String ruleSetKey;
        private List<PddSectionQuestions> sectionQuestions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PddSectionQuestions {
        private String sectionNumber;
        private int questionsCount;
    }
}
