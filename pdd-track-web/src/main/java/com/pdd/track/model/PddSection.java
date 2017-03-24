package com.pdd.track.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "PDD.TRACK.PDD_SECTION")
public class PddSection {
    private String key;
    private String number;
    private String name;
}
