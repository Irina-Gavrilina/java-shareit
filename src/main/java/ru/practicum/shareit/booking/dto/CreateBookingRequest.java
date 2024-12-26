package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBookingRequest {

    @NotNull
    @FutureOrPresent
    LocalDateTime start;
    @NotNull
    @Future
    LocalDateTime end;
    @NotNull
    Long itemId;

    @AssertTrue(message = "Задано неверное время начала бронирования")
    public boolean isStartBeforeEnd() {
        return start.isBefore(end);
    }
}