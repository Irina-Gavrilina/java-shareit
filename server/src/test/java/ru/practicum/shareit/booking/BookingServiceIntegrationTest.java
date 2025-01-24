package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.itemDto.CreateItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import java.time.LocalDateTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceIntegrationTest {

    final BookingServiceImpl bookingService;
    final UserServiceImpl userService;
    final ItemServiceImpl itemService;
    final EntityManager em;

    @Test
    public void createBookingItemIsAvailableTest() {
        CreateUserRequest createOwnerRequest = makeUserRequest("owner", "owner@mail.ru");
        userService.createUser(createOwnerRequest);

        TypedQuery<User> ownerQuery = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User owner = ownerQuery.setParameter("email", createOwnerRequest.getEmail())
                .getSingleResult();

        CreateUserRequest createBookerRequest = makeUserRequest("booker", "booker@mail.ru");
        userService.createUser(createBookerRequest);

        TypedQuery<User> bookerQuery  = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User booker = bookerQuery.setParameter("email", createBookerRequest.getEmail())
                .getSingleResult();

        CreateItemRequest createItemRequest = makeItemRequest("item1", "item1_description", true, null);
        itemService.createItem(createItemRequest, owner.getId());

        TypedQuery<Item> itemQuery = em.createQuery("SELECT i FROM Item i WHERE i.owner = :owner", Item.class);
        Item item = itemQuery.setParameter("owner", owner)
                .getSingleResult();

        CreateBookingRequest createBookingRequest = makeBookingRequest(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3), item.getId());
        bookingService.createBooking(createBookingRequest, booker.getId());

        TypedQuery<Booking> bookingQuery = em.createQuery("SELECT b FROM Booking b WHERE b.booker = :booker", Booking.class);
        Booking booking = bookingQuery.setParameter("booker", booker)
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(createBookingRequest.getStart()));
        assertThat(booking.getEnd(), equalTo(createBookingRequest.getEnd()));
        assertThat(booking.getItem(), equalTo(item));
        assertThat(booking.getBooker(), equalTo(booker));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    public void createBookingItemIsNotAvailableTest() {
        CreateUserRequest createOwnerRequest = makeUserRequest("owner", "owner@mail.ru");
        userService.createUser(createOwnerRequest);

        TypedQuery<User> ownerQuery = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User owner = ownerQuery.setParameter("email", createOwnerRequest.getEmail())
                .getSingleResult();

        CreateUserRequest createBookerRequest = makeUserRequest("booker", "booker@mail.ru");
        userService.createUser(createBookerRequest);

        TypedQuery<User> bookerQuery  = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User booker = bookerQuery.setParameter("email", createBookerRequest.getEmail())
                .getSingleResult();

        CreateItemRequest createItemRequest = makeItemRequest("item1", "item1_description", false, null);
        itemService.createItem(createItemRequest, owner.getId());

        TypedQuery<Item> itemQuery = em.createQuery("SELECT i FROM Item i WHERE i.owner = :owner", Item.class);
        Item item = itemQuery.setParameter("owner", owner)
                .getSingleResult();

        CreateBookingRequest createBookingRequest = makeBookingRequest(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3), item.getId());

        UnavailableBookingException exception = assertThrows(UnavailableBookingException.class,
                () -> bookingService.createBooking(createBookingRequest, booker.getId()));

        assertEquals("Данная вещь не доступна для аренды", exception.getMessage());
    }

    @Test
    public void createBookingWhenBookerIsAnOwnerTest() {
        CreateUserRequest createOwnerRequest = makeUserRequest("owner", "owner@mail.ru");
        userService.createUser(createOwnerRequest);

        TypedQuery<User> ownerQuery = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User owner = ownerQuery.setParameter("email", createOwnerRequest.getEmail())
                .getSingleResult();

        CreateItemRequest createItemRequest = makeItemRequest("item1", "item1_description", true, null);
        itemService.createItem(createItemRequest, owner.getId());

        TypedQuery<Item> itemQuery = em.createQuery("SELECT i FROM Item i WHERE i.owner = :owner", Item.class);
        Item item = itemQuery.setParameter("owner", owner)
                .getSingleResult();

        CreateBookingRequest createBookingRequest = makeBookingRequest(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3), item.getId());

        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> bookingService.createBooking(createBookingRequest, owner.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleForbiddenException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertEquals("Владелец не может забронировать свою вещь", exception.getMessage());
    }

    @Test
    public void createBookingItemWhenItemIsAlreadyBookedTest() {
        CreateUserRequest createOwnerRequest = makeUserRequest("owner", "owner@mail.ru");
        userService.createUser(createOwnerRequest);

        TypedQuery<User> ownerQuery = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User owner = ownerQuery.setParameter("email", createOwnerRequest.getEmail())
                .getSingleResult();

        CreateUserRequest createBookerRequest = makeUserRequest("booker", "booker@mail.ru");
        userService.createUser(createBookerRequest);

        TypedQuery<User> bookerQuery  = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User booker = bookerQuery.setParameter("email", createBookerRequest.getEmail())
                .getSingleResult();

        CreateItemRequest createItemRequest = makeItemRequest("item1", "item1_description", true, null);
        itemService.createItem(createItemRequest, owner.getId());

        TypedQuery<Item> itemQuery = em.createQuery("SELECT i FROM Item i WHERE i.owner = :owner", Item.class);
        Item item = itemQuery.setParameter("owner", owner)
                .getSingleResult();

        CreateBookingRequest createFirstBookingRequest = makeBookingRequest(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3), item.getId());
        bookingService.createBooking(createFirstBookingRequest, booker.getId());

        CreateBookingRequest createSecondBookingRequest = makeBookingRequest(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3), item.getId());

        UnavailableBookingException exception = assertThrows(UnavailableBookingException.class,
                () -> bookingService.createBooking(createSecondBookingRequest, booker.getId()));

        assertEquals("Даты другого бронирования частично или полностью перекрывают запрошенные",
                exception.getMessage());
    }

    @Test
    public void createBookingWhenUserNotFoundTest() {
        CreateUserRequest createOwnerRequest = makeUserRequest("owner", "owner@mail.ru");
        userService.createUser(createOwnerRequest);

        TypedQuery<User> ownerQuery = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User owner = ownerQuery.setParameter("email", createOwnerRequest.getEmail())
                .getSingleResult();

        CreateItemRequest createItemRequest = makeItemRequest("item1", "item1_description", true, null);
        itemService.createItem(createItemRequest, owner.getId());

        TypedQuery<Item> itemQuery = em.createQuery("SELECT i FROM Item i WHERE i.owner = :owner", Item.class);
        Item item = itemQuery.setParameter("owner", owner)
                .getSingleResult();

        CreateBookingRequest createbookingRequest = makeBookingRequest(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3), item.getId());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(createbookingRequest, 2L));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());
    }

    @Test
    public void createBookingWhenItemNotFoundTest() {
        CreateUserRequest createBookerRequest = makeUserRequest("booker", "booker@mail.ru");
        userService.createUser(createBookerRequest);

        TypedQuery<User> bookerQuery  = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User booker = bookerQuery.setParameter("email", createBookerRequest.getEmail())
                .getSingleResult();

        CreateBookingRequest createBookingRequest = makeBookingRequest(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3), 1L);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(createBookingRequest, booker.getId()));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());
    }

    private CreateUserRequest makeUserRequest(String name, String email) {
        CreateUserRequest request = new CreateUserRequest();
        request.setName(name);
        request.setEmail(email);
        return request;
    }

    private CreateItemRequest makeItemRequest(String name, String description, Boolean available, Long requestId) {
        CreateItemRequest request = new CreateItemRequest();
        request.setName(name);
        request.setDescription(description);
        request.setAvailable(available);
        request.setRequestId(requestId);
        return request;
    }

    private CreateBookingRequest makeBookingRequest(LocalDateTime start, LocalDateTime end, long itemId) {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setStart(start);
        request.setEnd(end);
        request.setItemId(itemId);
        return request;
    }
}