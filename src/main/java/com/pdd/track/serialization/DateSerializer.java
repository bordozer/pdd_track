package com.pdd.track.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.pdd.track.utils.Parameters;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(final LocalDate value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Parameters.DATE_FORMAT);
        jgen.writeString(value.format(formatter));
    }
}
