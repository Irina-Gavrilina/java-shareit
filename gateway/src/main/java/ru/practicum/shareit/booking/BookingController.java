package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.UnavailableBookingException;
import static ru.practicum.shareit.constants.Constants.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) long userId,
												@RequestBody @Valid CreateBookingRequest request) {
		log.info("Получен запрос POST на создание бронирования вещи {}", request);
		return bookingClient.createBooking(userId, request);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID_HEADER) long userId,
												 @NotNull @PathVariable("bookingId") long bookingId,
												 @NotNull @RequestParam("approved") boolean approved) {
		log.info("Получен запрос PATCH на подтверждение/отклонение бронирования с id = {}", bookingId);
		return bookingClient.approveBooking(bookingId, userId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@RequestHeader(USER_ID_HEADER) long userId,
												 @PathVariable Long bookingId) {
		log.info("Поступил запрос GET на получение данных о бронировании с id = {}", bookingId);
		return bookingClient.getBookingById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookingsByBookerId(@RequestHeader(USER_ID_HEADER) long userId,
														@RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
		BookingState state = BookingState.from(stateParam).orElseThrow(() ->
				new UnavailableBookingException("Введён некорректный статус бронирования" + stateParam));
		log.info("Поступил запрос GET на получение данных о всех бронированиях пользователя с id = {}", userId);
		return bookingClient.getBookingsByBookerId(userId, state);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsByItemOwnerId(@RequestHeader(USER_ID_HEADER) long userId,
														   @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
		BookingState state = BookingState.from(stateParam).orElseThrow(() ->
				new UnavailableBookingException("Введён некорректный статус бронирования" + stateParam));
		log.info("Поступил запрос GET на получение данных о всех бронированиях вещей пользователя с id = {}", userId);
		return  bookingClient.getBookingsByItemOwnerId(userId, state);
	}
}