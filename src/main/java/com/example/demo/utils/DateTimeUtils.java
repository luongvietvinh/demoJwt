package com.example.demo.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtils {

    // Format: có thể có hoặc không phần microseconds
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS]");

    public static Timestamp toTimestamp(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            LocalDateTime ldt = LocalDateTime.parse(value, FORMATTER);
            return Timestamp.valueOf(ldt);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid datetime format, expected yyyy-MM-dd HH:mm:ss[.SSSSSS]", e);
        }
    }

    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime().format(FORMATTER);
    }
}
