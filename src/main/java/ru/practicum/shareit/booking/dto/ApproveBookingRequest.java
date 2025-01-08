package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApproveBookingRequest {

    @NotNull
    Long bookingId;
    @NotNull
    Long ownerId;
    @NotNull
    Boolean approved;
}