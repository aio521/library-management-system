package com.library.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class DateUtil {
    private DateUtil() {}

    public static long daysBetween(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to);
    }
}
