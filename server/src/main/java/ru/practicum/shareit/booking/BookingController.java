package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.ApproveBookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import java.util.List;
import static ru.practicum.shareit.constants.Constants.USER_ID_HEADER;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestBody CreateBookingRequest request) {
        log.info("Получен запрос POST на создание бронирования вещи {}", request);
        return bookingService.createBooking(request, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse approveBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable("bookingId") long bookingId,
                                          @RequestParam("approved") boolean approved) {
        log.info("Получен запрос PATCH на подтверждение/отклонение бронирования с id = {}", bookingId);
        ApproveBookingRequest request = ApproveBookingRequest.builder()
                .bookingId(bookingId)
                .ownerId(userId)
                .approved(approved)
                .build();
        return bookingService.approveBooking(request);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBookingById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable("bookingId") long bookingId) {
        log.info("Поступил запрос GET на получение данных о бронировании с id = {}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponse> getBookingsByBookerId(@RequestHeader(USER_ID_HEADER) long userId,
                                                       @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Поступил запрос GET на получение данных о всех бронированиях пользователя с id = {}", userId);
        return bookingService.getBookingsByBookerId(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingsByItemOwnerId(@RequestHeader(USER_ID_HEADER) long userId,
                                                          @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Поступил запрос GET на получение данных о всех бронированиях вещей пользователя с id = {}", userId);
        return bookingService.getBookingsByItemOwnerId(state, userId);
    }
}