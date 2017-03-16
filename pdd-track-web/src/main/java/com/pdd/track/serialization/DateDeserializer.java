package com.pdd.track.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.pdd.track.utils.Parameters;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Parameters.DATE_FORMAT);
        return LocalDate.parse(jp.getText(), formatter);
    }
}
