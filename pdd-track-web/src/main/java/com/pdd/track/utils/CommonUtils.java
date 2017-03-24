package com.pdd.track.utils;

import com.pdd.track.model.Testing;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtils {

    public static String UUID() {
        return UUID.randomUUID().toString();
    }

    public static String formatDouble(final double value) {
        return value == 0 ? "" : new DecimalFormat("#0.00").format(value);
    }

    public static double getPercentage(final Testing testing) {
        return (double) testing.getPassedQuestions() / testing.getTotalQuestions() * 100;
    }

    public static long ageInDays(final LocalDate date, final LocalDate onDate) {
        return ChronoUnit.DAYS.between(date, onDate);
    }
}
