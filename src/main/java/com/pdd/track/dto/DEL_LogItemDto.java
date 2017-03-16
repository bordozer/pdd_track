package com.pdd.track.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pdd.track.serialization.DateTimeDeserializer;
import com.pdd.track.serialization.DateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DEL_LogItemDto {
    private String id;
    private String field;
    private String value;
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime time;
}
