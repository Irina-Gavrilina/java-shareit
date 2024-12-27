package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {

    Long id;
    @NotBlank
    @Size(max = 512)
    String description;
    @NotNull
    User requester;
    @NotNull
    @FutureOrPresent
    LocalDateTime created;
}