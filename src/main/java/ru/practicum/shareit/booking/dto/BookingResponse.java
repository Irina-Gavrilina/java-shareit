package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.itemDto.ItemResponse;
import ru.practicum.shareit.user.dto.UserResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {

    Long id;
    String start;
    String end;
    ItemResponse item;
    UserResponse booker;
    BookingStatus status;
}