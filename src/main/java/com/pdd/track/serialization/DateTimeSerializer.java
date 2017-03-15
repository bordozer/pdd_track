package com.pdd.track.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(final LocalDateTime value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        jgen.writeString(value.atZone(ZoneOffset.UTC).toString());
    }
}
