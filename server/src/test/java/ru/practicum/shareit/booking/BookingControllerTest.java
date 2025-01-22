package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.ApproveBookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.itemDto.ItemResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.shareit.constants.Constants.FORMATTER;
import static ru.practicum.shareit.constants.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    User owner;
    Item item;
    ItemResponse itemResponse;
    User booker;
    UserResponse bookerResponse;

    @BeforeEach
    public void setUp() {
        owner = new User(1L, "user1", "user1@mail.ru");
        item = new Item(1L, "item1", "item1_description", true, owner, null);
        itemResponse = new ItemResponse(item.getId(), item.getName(), item.getDescription(), true, null,
                null);
        booker = new User(2L, "user2", "user2@mail.ru");
        bookerResponse = new UserResponse(booker.getId(), booker.getName(), booker.getEmail());
    }

    @Test
    public void createBookingTest() throws Exception {
        CreateBookingRequest createBookingRequest = new CreateBookingRequest(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3), item.getId());
        BookingResponse bookingResponse = new BookingResponse(1L, FORMATTER.format(createBookingRequest.getStart()),
                FORMATTER.format(createBookingRequest.getEnd()), itemResponse, bookerResponse, BookingStatus.WAITING);

        when(bookingService.createBooking(createBookingRequest, booker.getId()))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, booker.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponse.getStart())))
                .andExpect(jsonPath("$.end", is(bookingResponse.getEnd())))
                .andExpect(jsonPath("$.item", is(bookingResponse.getItem()), ItemResponse.class))
                .andExpect(jsonPath("$.booker", is(bookingResponse.getBooker()), UserResponse.class))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingResponse.getStatus()))));

        verify(bookingService, Mockito.times(1)).createBooking(createBookingRequest, booker.getId());
    }

    @Test
    public void approveBookingTest() throws Exception {
        Booking notApprovedBooking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3),
                item, booker, BookingStatus.WAITING);
        ApproveBookingRequest approveBookingRequest = new ApproveBookingRequest(notApprovedBooking.getId(),
                owner.getId(), true);
        Booking approvedBooking = new Booking(notApprovedBooking.getId(), notApprovedBooking.getStart(),
                notApprovedBooking.getEnd(), notApprovedBooking.getItem(), notApprovedBooking.getBooker(),
                BookingStatus.APPROVED);
        BookingResponse bookingResponse = new BookingResponse(1L, FORMATTER.format(approvedBooking.getStart()),
                FORMATTER.format(approvedBooking.getEnd()), itemResponse, bookerResponse, BookingStatus.APPROVED);

        when(bookingService.approveBooking(approveBookingRequest))
                .thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/{bookingId}", notApprovedBooking.getId())
                        .content(mapper.writeValueAsString(approveBookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(approveBookingRequest.getApproved()))
                        .header(USER_ID_HEADER, owner.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponse.getStart())))
                .andExpect(jsonPath("$.end", is(bookingResponse.getEnd())))
                .andExpect(jsonPath("$.item", is(bookingResponse.getItem()), ItemResponse.class))
                .andExpect(jsonPath("$.booker", is(bookingResponse.getBooker()), UserResponse.class))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingResponse.getStatus()))));

        verify(bookingService, Mockito.times(1)).approveBooking(approveBookingRequest);
    }

    @Test
    public void getBookingByIdTest() throws Exception {
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), item,
                booker, BookingStatus.APPROVED);
        BookingResponse bookingResponse = new BookingResponse(1L, FORMATTER.format(booking.getStart()),
                FORMATTER.format(booking.getEnd()), itemResponse, bookerResponse, BookingStatus.APPROVED);

        when(bookingService.getBookingById(booking.getId(), booker.getId()))
                .thenReturn(bookingResponse);

        mvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header(USER_ID_HEADER, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponse.getStart())))
                .andExpect(jsonPath("$.end", is(bookingResponse.getEnd())))
                .andExpect(jsonPath("$.item", is(bookingResponse.getItem()), ItemResponse.class))
                .andExpect(jsonPath("$.booker", is(bookingResponse.getBooker()), UserResponse.class))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingResponse.getStatus()))));

        verify(bookingService, Mockito.times(1)).getBookingById(booking.getId(), booker.getId());
    }

    @Test
    public void getBookingsByBookerIdTest() throws Exception {
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), item,
                booker, BookingStatus.APPROVED);
        BookingResponse bookingResponse = new BookingResponse(1L, FORMATTER.format(booking.getStart()),
                FORMATTER.format(booking.getEnd()), itemResponse, bookerResponse, BookingStatus.APPROVED);

        when(bookingService.getBookingsByBookerId(anyString(), anyLong()))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings")
                        .param("state", String.valueOf(BookingState.CURRENT))
                        .header(USER_ID_HEADER, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponse.getStart())))
                .andExpect(jsonPath("$[0].end", is(bookingResponse.getEnd())))
                .andExpect(jsonPath("$[0].item", is(bookingResponse.getItem()), ItemResponse.class))
                .andExpect(jsonPath("$[0].booker", is(bookingResponse.getBooker()), UserResponse.class))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(bookingResponse.getStatus()))));

        verify(bookingService, Mockito.times(1)).getBookingsByBookerId(anyString(), anyLong());
    }

    @Test
    public void getBookingsByItemOwnerIdTest() throws Exception {
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), item,
                booker, BookingStatus.APPROVED);
        BookingResponse bookingResponse = new BookingResponse(1L, FORMATTER.format(booking.getStart()),
                FORMATTER.format(booking.getEnd()), itemResponse, bookerResponse, BookingStatus.APPROVED);

        when(bookingService.getBookingsByItemOwnerId(anyString(), anyLong()))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(get("/bookings/owner")
                        .param("state", String.valueOf(BookingState.CURRENT))
                        .header(USER_ID_HEADER, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponse.getStart())))
                .andExpect(jsonPath("$[0].end", is(bookingResponse.getEnd())))
                .andExpect(jsonPath("$[0].item", is(bookingResponse.getItem()), ItemResponse.class))
                .andExpect(jsonPath("$[0].booker", is(bookingResponse.getBooker()), UserResponse.class))
                .andExpect(jsonPath("$[0].status", is(String.valueOf(bookingResponse.getStatus()))));

        verify(bookingService, Mockito.times(1)).getBookingsByItemOwnerId(anyString(), anyLong());
    }
}