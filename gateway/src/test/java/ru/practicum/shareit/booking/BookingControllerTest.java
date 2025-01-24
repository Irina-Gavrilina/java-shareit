package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import java.time.LocalDateTime;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    @Test
    public void createBookingWhenInvalidDateTest() throws Exception {
        CreateBookingRequest createBookingRequest = new CreateBookingRequest(LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(3), 1L);

        mvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequest))
                        .header(USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Произошла непредвиденная ошибка")));

        verify(bookingClient, never()).createBooking(anyLong(), any(CreateBookingRequest.class));
    }
}