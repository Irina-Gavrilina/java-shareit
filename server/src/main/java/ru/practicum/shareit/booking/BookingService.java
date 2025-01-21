package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.ApproveBookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(CreateBookingRequest request, long userId);

    BookingResponse approveBooking(ApproveBookingRequest request);

    BookingResponse getBookingById(long bookingId, long userId);

    List<BookingResponse> getBookingsByBookerId(String state, long bookerId);

    List<BookingResponse> getBookingsByItemOwnerId(String state, long userId);
}
