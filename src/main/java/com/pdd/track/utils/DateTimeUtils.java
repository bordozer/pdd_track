package com.pdd.track.utils;

import com.sun.istack.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtils {

    public static String formatTime(@Nullable final LocalDateTime time) {
        if (time == null) {
            return null;
        }
        try {
            return time.atZone(ZoneOffset.UTC).toString(); // TODO: the same in DTO module (DateTimeSerializer)
        } catch (DateTimeParseException e) {
            LOGGER.info("Cannot format time: {}", time);
            return null;
        }
    }

    public static LocalDateTime parseTime(@Nullable final String time) {
        if (time == null) {
            return null;
        }
        try {
            return ZonedDateTime.parse(time).toLocalDateTime(); // TODO: the same in DTO module (DateTimeDeserializer)
        } catch (DateTimeParseException e) {
            LOGGER.info("Cannot parse time: {}", time);
            return null;
        }
    }
}
