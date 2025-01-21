package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.ApproveBookingRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableBookingException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(
                "Пользователя с id = %d нет в базе", userId)));
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new NotFoundException(String.format("Вещи с id " +
                "= %d нет в базе", request.getItemId())));

        if (!item.getAvailable()) {
            throw new UnavailableBookingException("Данная вещь не доступна для аренды");
        }
        if (item.getOwner().equals(user)) {
            throw new ForbiddenException("Владелец не может забронировать свою вещь");
        }

        boolean isAlreadyBooked = bookingRepository.existsByItemIdAndEndAfterAndStartBefore(item.getId(),
                request.getStart(), request.getEnd());

        if (isAlreadyBooked) {
            throw new UnavailableBookingException("Даты другого бронирования частично или полностью перекрывают " +
                    "запрошенные");
        }

        Booking booking = Booking.builder()
                .start(request.getStart())
                .end(request.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        return BookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse approveBooking(ApproveBookingRequest request) {
        Booking booking = bookingRepository.findByIdWithItem(request.getBookingId()).orElseThrow(() ->
                new NotFoundException(String.format("Бронирования с id = %d нет в базе", request.getBookingId())));
        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(request.getOwnerId())) {
            throw new UnavailableBookingException("Подтвердить/Отклонить бронирование может только владелец вещи");
        }

        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (request.getApproved()) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new UnavailableBookingException(String.format("Бронирование с id = %d находится в статусе %s",
                    booking.getId(), booking.getStatus()));
        }
        return BookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse getBookingById(long bookingId, long userId) {
        Booking booking = bookingRepository.findByIdWithItem(bookingId).orElseThrow(() -> new NotFoundException(String.format(
                        "Бронирования с id = %d нет в базе", bookingId)));
        Item item = booking.getItem();

        if (booking.getBooker().getId().equals(userId) || item.getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingResponse(booking);
        } else {
            throw new UnavailableBookingException("Получить данные о бронировании может только владелец вещи или " +
                    "автор бронирования");
        }
    }

    @Override
    public List<BookingResponse> getBookingsByBookerId(String state, long bookerId) {
        User user = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException(String.format(
                "Пользователя с id = %d нет в базе", bookerId)));
        List<Booking> bookings;
        BookingState bookingState = BookingState.from(state.toUpperCase()).orElseThrow(() ->
                new UnavailableBookingException("Введён некорректный статус бронирования"));

        bookings = switch (bookingState) {
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(user.getId(),
                    LocalDateTime.now(), LocalDateTime.now());
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(user.getId(), LocalDateTime.now());
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(user.getId(), LocalDateTime.now());
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.REJECTED);
            default -> bookingRepository.findByBookerIdOrderByStartDesc(user.getId());
        };
        return BookingMapper.toListOfBookingsResponse(bookings);
    }

    @Override
    public List<BookingResponse> getBookingsByItemOwnerId(String state, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(
                "Пользователя с id = %d нет в базе", userId)));
        List<Booking> bookings;
        BookingState bookingState = BookingState.from(state.toUpperCase()).orElseThrow(() ->
                new UnavailableBookingException("Введён некорректный статус бронирования"));

        bookings = switch (bookingState) {
            case CURRENT -> bookingRepository.getCurrentBookingsByOwnerIdOrderByStartDesc(user.getId(), LocalDateTime.now());
            case PAST -> bookingRepository.getPastBookingsByOwnerIdOrderByStartDesc(user.getId(), LocalDateTime.now());
            case FUTURE -> bookingRepository.getFutureBookingsByOwnerIdOrderByStartDesc(user.getId(), LocalDateTime.now());
            case WAITING -> bookingRepository.getBookingsByOwnerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING);
            case REJECTED -> bookingRepository.getBookingsByOwnerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.REJECTED);
            default -> bookingRepository.getAllBookingsByOwnerIdOrderByStartDesc(user.getId());
        };
        return BookingMapper.toListOfBookingsResponse(bookings);
    }
}