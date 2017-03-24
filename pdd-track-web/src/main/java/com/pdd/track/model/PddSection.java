package com.pdd.track.model;

import com.pdd.track.utils.CommonUtils;
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

    public PddSection(final String number, final String name) {
        this.key = CommonUtils.UUID();
        this.number = number;
        this.name = name;
    }
}
