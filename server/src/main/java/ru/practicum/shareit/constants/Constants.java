package ru.practicum.shareit.constants;

import java.time.format.DateTimeFormatter;

public class Constants {

    private Constants() {
    }

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
}