package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;
import java.util.ArrayList;
import java.util.List;
import static ru.practicum.shareit.constants.Constants.FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingResponse toBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .start(FORMATTER.format(booking.getStart()))
                .end(FORMATTER.format(booking.getEnd()))
                .item(ItemMapper.toItemResponse(booking.getItem()))
                .booker(UserMapper.toUserResponse(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingShortResponse toBookingShortResponse(Booking booking) {
        return BookingShortResponse.builder()
                .id(booking.getId())
                .start(FORMATTER.format(booking.getStart()))
                .end(FORMATTER.format(booking.getEnd()))
                .booker(UserMapper.toUserResponse(booking.getBooker()))
                .build();
    }

    public static List<BookingResponse> toListOfBookingsResponse(List<Booking> bookings) {
        if (bookings == null) {
            return new ArrayList<>();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingResponse)
                .toList();
    }
}