package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.ApproveBookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableBookingException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.itemDto.ItemResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.constants.Constants.FORMATTER;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImplTest {

    @InjectMocks
    BookingServiceImpl bookingService;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    User owner;
    User booker;
    Item item;
    Booking notAppvovedBooking;
    Booking appvovedBooking;
    ApproveBookingRequest approveBookingRequest;
    BookingResponse bookingResponse;

    @BeforeEach
    public void setUp() {
        owner = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        booker = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("item1")
                .description("item1_description")
                .available(true)
                .owner(owner)
                .build();

        notAppvovedBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        appvovedBooking = Booking.builder()
                .id(1L)
                .start(notAppvovedBooking.getStart())
                .end(notAppvovedBooking.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        approveBookingRequest = ApproveBookingRequest.builder()
                .bookingId(1L)
                .ownerId(1L)
                .approved(true)
                .build();

        bookingResponse = BookingResponse.builder()
                .id(1L)
                .start(FORMATTER.format(notAppvovedBooking.getStart()))
                .end(FORMATTER.format(notAppvovedBooking.getEnd()))
                .item(new ItemResponse(1L, "item1", "item1_description", true, null, null))
                .booker(new UserResponse(2L, "user2", "user2@mail.ru"))
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    public void approveBookingWhenBookingExistsAndBookingStatusIsWaitingTest() {
        when(bookingRepository.findByIdWithItem(approveBookingRequest.getBookingId()))
                .thenReturn(Optional.of(notAppvovedBooking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(appvovedBooking);

        BookingResponse result = bookingService.approveBooking(approveBookingRequest);

        assertNotNull(result);
        assertEquals(bookingResponse, result);
        verify(bookingRepository, Mockito.times(1)).findByIdWithItem(notAppvovedBooking.getId());
        verify(bookingRepository, Mockito.times(1)).save(any(Booking.class));
    }

    @Test
    public void approveBookingIsRejectedWhenBookingExistsAndBookingStatusIsWaitingTest() {
        ApproveBookingRequest approveBookingRequest = ApproveBookingRequest.builder()
                .bookingId(1L)
                .ownerId(1L)
                .approved(false)
                .build();

        appvovedBooking = Booking.builder()
                .id(1L)
                .start(notAppvovedBooking.getStart())
                .end(notAppvovedBooking.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(1L)
                .start(FORMATTER.format(notAppvovedBooking.getStart()))
                .end(FORMATTER.format(notAppvovedBooking.getEnd()))
                .item(new ItemResponse(1L, "item1", "item1_description", true, null, null))
                .booker(new UserResponse(2L, "user2", "user2@mail.ru"))
                .status(BookingStatus.REJECTED)
                .build();

        when(bookingRepository.findByIdWithItem(approveBookingRequest.getBookingId()))
                .thenReturn(Optional.of(notAppvovedBooking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(appvovedBooking);

        BookingResponse result = bookingService.approveBooking(approveBookingRequest);

        assertNotNull(result);
        assertEquals(bookingResponse, result);
        verify(bookingRepository, Mockito.times(1)).findByIdWithItem(notAppvovedBooking.getId());
        verify(bookingRepository, Mockito.times(1)).save(any(Booking.class));
    }

    @Test
    public void approveBookingWhenBookingExistsAndBookingStatusIsNotWaitingTest() {
        Booking notAppvovedBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.CANCELED)
                .build();

        when(bookingRepository.findByIdWithItem(approveBookingRequest.getBookingId()))
                .thenReturn(Optional.of(notAppvovedBooking));

        UnavailableBookingException exception = assertThrows(UnavailableBookingException.class,
                () -> bookingService.approveBooking(approveBookingRequest));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleUnavailableBookingException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        verify(bookingRepository, Mockito.times(1)).findByIdWithItem(approveBookingRequest.getBookingId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approveBookingWhenBookingNotFoundTest() {
        when(bookingRepository.findByIdWithItem(approveBookingRequest.getBookingId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(approveBookingRequest));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(notAppvovedBooking.getId())));
        verify(bookingRepository, Mockito.times(1)).findByIdWithItem(approveBookingRequest.getBookingId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approveBookingWhenBookingExistsAndUserIsNotAnOwnerTest() {
        ApproveBookingRequest approveBookingRequest = ApproveBookingRequest.builder()
                .bookingId(1L)
                .ownerId(2L)
                .approved(true)
                .build();

        when(bookingRepository.findByIdWithItem(approveBookingRequest.getBookingId()))
                .thenReturn(Optional.of(notAppvovedBooking));

        UnavailableBookingException exception = assertThrows(UnavailableBookingException.class,
                () -> bookingService.approveBooking(approveBookingRequest));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleUnavailableBookingException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        verify(bookingRepository, Mockito.times(1)).findByIdWithItem(approveBookingRequest.getBookingId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void getBookingByIdWhenBookingExistsAndRequesterIsABookerTest() {
        when(bookingRepository.findByIdWithItem(approveBookingRequest.getBookingId()))
                .thenReturn(Optional.of(appvovedBooking));

        BookingResponse result = bookingService.getBookingById(appvovedBooking.getId(), booker.getId());

        assertEquals(bookingResponse, result);
        verify(bookingRepository, Mockito.times(1)).findByIdWithItem(approveBookingRequest.getBookingId());
    }

    @Test
    public void getBookingByIdWhenBookingExistsAndRequesterIsAnOwnerTest() {
        when(bookingRepository.findByIdWithItem(approveBookingRequest.getBookingId()))
                .thenReturn(Optional.of(appvovedBooking));

        BookingResponse result = bookingService.getBookingById(appvovedBooking.getId(), owner.getId());

        assertEquals(bookingResponse, result);
        verify(bookingRepository, Mockito.times(1)).findByIdWithItem(approveBookingRequest.getBookingId());
    }

    @Test
    public void getBookingByIdWhenBookingExistsAndRequesterIsNotAnOwnerOrBookerTest() {
        User user = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@mail.ru")
                .build();

        when(bookingRepository.findByIdWithItem(approveBookingRequest.getBookingId()))
                .thenReturn(Optional.of(appvovedBooking));

        UnavailableBookingException exception = assertThrows(UnavailableBookingException.class,
                        () -> bookingService.getBookingById(appvovedBooking.getId(), user.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleUnavailableBookingException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        verify(bookingRepository, Mockito.times(1)).findByIdWithItem(approveBookingRequest.getBookingId());
    }

    @Test
    public void getBookingByIdWhenBookingNotFoundTest() {
       when(bookingRepository.findByIdWithItem(approveBookingRequest.getBookingId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(appvovedBooking.getId(), booker.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        verify(bookingRepository, Mockito.times(1)).findByIdWithItem(approveBookingRequest.getBookingId());
    }

    @Test
    public void getBookingsByBookerIdWhenUserExistsAndStateIsCurrentTest() {
        Booking booking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(2L)
                .start(FORMATTER.format(booking.getStart()))
                .end(FORMATTER.format(booking.getEnd()))
                .item(new ItemResponse(1L, "item1", "item1_description", true, null, null))
                .booker(new UserResponse(2L, "user2", "user2@mail.ru"))
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingResponse> result = bookingService.getBookingsByBookerId("Current", booker.getId());

        assertNotNull(result);
        assertEquals(List.of(bookingResponse), result);
        verify(userRepository, Mockito.times(1)).findById(booker.getId());
        verify(bookingRepository, Mockito.times(1)).findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    public void getBookingsByBookerIdWhenUserExistsAndStateIsPastTest() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(appvovedBooking));

        List<BookingResponse> result = bookingService.getBookingsByBookerId("Past", booker.getId());

        assertNotNull(result);
        assertEquals(List.of(bookingResponse), result);
        verify(userRepository, Mockito.times(1)).findById(booker.getId());
        verify(bookingRepository, Mockito.times(1)).findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(),
                any(LocalDateTime.class));
    }

    @Test
    public void getBookingsByBookerIdWhenUserExistsAndStateIsFutureTest() {
        Booking booking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(2L)
                .start(FORMATTER.format(booking.getStart()))
                .end(FORMATTER.format(booking.getEnd()))
                .item(new ItemResponse(1L, "item1", "item1_description", true, null, null))
                .booker(new UserResponse(2L, "user2", "user2@mail.ru"))
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingResponse> result = bookingService.getBookingsByBookerId("Future", booker.getId());

        assertNotNull(result);
        assertEquals(List.of(bookingResponse), result);
        verify(userRepository, Mockito.times(1)).findById(booker.getId());
        verify(bookingRepository, Mockito.times(1)).findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class));
    }

    @Test
    public void getBookingsByBookerIdWhenUserExistsAndStateIsWaitingTest() {
        BookingResponse bookingResponse = BookingResponse.builder()
                .id(1L)
                .start(FORMATTER.format(notAppvovedBooking.getStart()))
                .end(FORMATTER.format(notAppvovedBooking.getEnd()))
                .item(new ItemResponse(1L, "item1", "item1_description", true, null, null))
                .booker(new UserResponse(2L, "user2", "user2@mail.ru"))
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(notAppvovedBooking));

        List<BookingResponse> result = bookingService.getBookingsByBookerId("WAITING", booker.getId());

        assertNotNull(result);
        assertEquals(List.of(bookingResponse), result);
        verify(userRepository, Mockito.times(1)).findById(booker.getId());
        verify(bookingRepository, Mockito.times(1)).findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class));
    }

    @Test
    public void getBookingsByBookerIdWhenUserExistsAndStateIsRejectedTest() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(1L)
                .start(FORMATTER.format(notAppvovedBooking.getStart()))
                .end(FORMATTER.format(notAppvovedBooking.getEnd()))
                .item(new ItemResponse(1L, "item1", "item1_description", true, null, null))
                .booker(new UserResponse(2L, "user2", "user2@mail.ru"))
                .status(BookingStatus.REJECTED)
                .build();

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        List<BookingResponse> result = bookingService.getBookingsByBookerId("REJECTED", booker.getId());

        assertNotNull(result);
        assertEquals(List.of(bookingResponse), result);
        verify(userRepository, Mockito.times(1)).findById(booker.getId());
        verify(bookingRepository, Mockito.times(1)).findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class));
    }

    @Test
    public void getBookingsByBookerIdWhenUserExistsAndStateIsDefaultTest() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(booker.getId()))
                .thenReturn(List.of(appvovedBooking));

        List<BookingResponse> result = bookingService.getBookingsByBookerId("ALL", booker.getId());

        assertNotNull(result);
        assertEquals(List.of(bookingResponse), result);
        verify(userRepository, Mockito.times(1)).findById(booker.getId());
        verify(bookingRepository, Mockito.times(1)).findByBookerIdOrderByStartDesc(booker.getId());
    }

    @Test
    public void getBookingsByBookerIdWhenUserNotFoundTest() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByBookerId("ALL", booker.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        verify(userRepository, Mockito.times(1)).findById(booker.getId());
    }

    @Test
    public void getBookingsByBookerIdWhenUserExistsAndStateIsIncorrectTest() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));

        UnavailableBookingException exception = assertThrows(UnavailableBookingException.class,
                () -> bookingService.getBookingsByBookerId("Incorrect_State", booker.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleUnavailableBookingException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        verify(userRepository, Mockito.times(1)).findById(booker.getId());
    }

    @Test
    public void getBookingsByItemOwnerIdWhenUserExistsAndStateIsCurrentTest() {
        Booking booking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(2L)
                .start(FORMATTER.format(booking.getStart()))
                .end(FORMATTER.format(booking.getEnd()))
                .item(new ItemResponse(1L, "item1", "item1_description", true, null, null))
                .booker(new UserResponse(2L, "user2", "user2@mail.ru"))
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getCurrentBookingsByOwnerIdOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingResponse> result = bookingService.getBookingsByItemOwnerId("Current", owner.getId());

        assertNotNull(result);
        assertEquals(List.of(bookingResponse), result);
        verify(userRepository, Mockito.times(1)).findById(owner.getId());
        verify(bookingRepository, Mockito.times(1)).getCurrentBookingsByOwnerIdOrderByStartDesc(anyLong(), any(LocalDateTime.class));
    }

    @Test
    public void getBookingsByItemOwnerIdWhenThereAreNoBookingsTest() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getCurrentBookingsByOwnerIdOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);

        List<BookingResponse> result = bookingService.getBookingsByItemOwnerId("Current", owner.getId());

        assertNotNull(result);
        assertEquals(List.of(), result);
        verify(userRepository, Mockito.times(1)).findById(owner.getId());
        verify(bookingRepository, Mockito.times(1)).getCurrentBookingsByOwnerIdOrderByStartDesc(anyLong(), any(LocalDateTime.class));
    }

    @Test
    public void getBookingsByItemOwnerIdWhenUserExistsAndStateIsPastTest() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getPastBookingsByOwnerIdOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(appvovedBooking));

        List<BookingResponse> result = bookingService.getBookingsByItemOwnerId("Past", owner.getId());

        assertNotNull(result);
        assertEquals(List.of(bookingResponse), result);
        verify(userRepository, Mockito.times(1)).findById(owner.getId());
        verify(bookingRepository, Mockito.times(1)).getPastBookingsByOwnerIdOrderByStartDesc(anyLong(), any(LocalDateTime.class));
    }

    @Test
    public void getBookingsByItemOwnerIdWhenUserExistsAndStateIsFutureTest() {
        Booking booking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(2L)
                .start(FORMATTER.format(booking.getStart()))
                .end(FORMATTER.format(booking.getEnd()))
                .item(new ItemResponse(1L, "item1", "item1_description", true, null, null))
                .booker(new UserResponse(2L, "user2", "user2@mail.ru"))
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getFutureBookingsByOwnerIdOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingResponse> result = bookingService.getBookingsByItemOwnerId("Future", owner.getId());

        assertNotNull(result);
        assertEquals(List.of(bookingResponse), result);
        verify(userRepository, Mockito.times(1)).findById(owner.getId());
        verify(bookingRepository, Mockito.times(1)).getFutureBookingsByOwnerIdOrderByStartDesc(anyLong(), any(LocalDateTime.class));
    }

    @Test
    public void getBookingsByItemOwnerIdWhenUserExistsAndStateIsWaitingTest() {
        BookingResponse bookingResponse = BookingResponse.builder()
                .id(1L)
                .start(FORMATTER.format(notAppvovedBooking.getStart()))
                .end(FORMATTER.format(notAppvovedBooking.getEnd()))
                .item(new ItemResponse(1L, "item1", "item1_description", true, null, null))
                .booker(new UserResponse(2L, "user2", "user2@mail.ru"))
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getBookingsByOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(notAppvovedBooking));

        List<BookingResponse> result = bookingService.getBookingsByItemOwnerId("WAITING", owner.getId());

        assertNotNull(result);
        assertEquals(List.of(bookingResponse), result);
        verify(userRepository, Mockito.times(1)).findById(owner.getId());
        verify(bookingRepository, Mockito.times(1)).getBookingsByOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class));
    }

    @Test
    public void getBookingsByItemOwnerIdWhenUserExistsAndStateIsRejectedTest() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(1L)
                .start(FORMATTER.format(notAppvovedBooking.getStart()))
                .end(FORMATTER.format(notAppvovedBooking.getEnd()))
                .item(new ItemResponse(1L, "item1", "item1_description", true, null, null))
                .booker(new UserResponse(2L, "user2", "user2@mail.ru"))
                .status(BookingStatus.REJECTED)
                .build();

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getBookingsByOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        List<BookingResponse> result = bookingService.getBookingsByItemOwnerId("REJECTED", owner.getId());

        assertNotNull(result);
        assertEquals(List.of(bookingResponse), result);
        verify(userRepository, Mockito.times(1)).findById(owner.getId());
        verify(bookingRepository, Mockito.times(1)).getBookingsByOwnerIdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class));
    }

    @Test
    public void getBookingsByItemOwnerIdWhenUserExistsAndStateIsDefaultTest() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getAllBookingsByOwnerIdOrderByStartDesc(owner.getId()))
                .thenReturn(List.of(appvovedBooking));

        List<BookingResponse> result = bookingService.getBookingsByItemOwnerId("ALL", owner.getId());

        assertNotNull(result);
        assertEquals(List.of(bookingResponse), result);
        verify(userRepository, Mockito.times(1)).findById(owner.getId());
        verify(bookingRepository, Mockito.times(1)).getAllBookingsByOwnerIdOrderByStartDesc(owner.getId());
    }

    @Test
    public void getBookingsByItemOwnerIdWhenUserNotFoundTest() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByItemOwnerId("ALL", owner.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        verify(userRepository, Mockito.times(1)).findById(owner.getId());
    }

    @Test
    public void getBookingsByItemOwnerIdWhenUserExistsAndStateIsIncorrectTest() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        UnavailableBookingException exception = assertThrows(UnavailableBookingException.class,
                () -> bookingService.getBookingsByItemOwnerId("Incorrect_State", owner.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleUnavailableBookingException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        verify(userRepository, Mockito.times(1)).findById(owner.getId());
    }
}