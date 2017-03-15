package com.pdd.track.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogItem {
    private String id;
    private String field;
    private String value;
    private LocalDateTime time;
}
