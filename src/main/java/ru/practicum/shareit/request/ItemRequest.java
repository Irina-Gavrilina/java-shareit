package ru.practicum.shareit.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {

    Long id;
    @NotBlank
    String description;
    @NotBlank
    User requester;
    @NotNull
    @FutureOrPresent
    LocalDateTime created;
}