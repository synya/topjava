package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static <T extends Comparable<T>> boolean isBetween(T dateTime, T startDateTime, T endDateTime) {
        return dateTime.compareTo(startDateTime) >= 0 && dateTime.compareTo(endDateTime) <= 0;
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }

    public static LocalDate parseDate(String string, LocalDate defaultDate) {
        try {
            return (string == null) ? defaultDate : LocalDate.parse(string, DateTimeUtil.DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return defaultDate;
        }
    }

    public static LocalTime parseTime(String string, LocalTime defaultTime) {
        try {
            return (string == null) ? defaultTime : LocalTime.parse(string, DateTimeUtil.TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return defaultTime;
        }
    }
}
