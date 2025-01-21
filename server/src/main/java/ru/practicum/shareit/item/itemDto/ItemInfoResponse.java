package ru.practicum.shareit.item.itemDto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingShortResponse;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemInfoResponse {

    Long id;
    String name;
    String description;
    Boolean available;
    Long request;
    BookingShortResponse lastBooking;
    BookingShortResponse nextBooking;
    List<CommentResponse> comments;
}