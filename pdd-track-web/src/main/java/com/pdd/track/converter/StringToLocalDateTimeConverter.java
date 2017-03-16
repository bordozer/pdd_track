package com.pdd.track.converter;

import com.pdd.track.utils.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;

public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
    @Override
    public LocalDateTime convert(String source) {
        return source == null ? null : DateTimeUtils.parseTime(source);
    }
}
