package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.ApproveBookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingShortResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.itemDto.ItemResponse;
import ru.practicum.shareit.user.dto.UserResponse;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingJsonTest {

    final JacksonTester<ApproveBookingRequest> approveBookingRequestJacksonTester;
    final JacksonTester<BookingResponse> bookingResponseJacksonTester;
    final JacksonTester<BookingShortResponse> bookingShortResponseJacksonTester;
    final JacksonTester<CreateBookingRequest> createBookingRequestJacksonTester;

    @Test
    void testApproveBookingRequest() throws Exception {
        ApproveBookingRequest approveBookingRequest = ApproveBookingRequest.builder()
                .bookingId(1L)
                .bookingId(1L)
                .approved(true)
                .build();

        JsonContent<ApproveBookingRequest> result = approveBookingRequestJacksonTester.write(approveBookingRequest);

        assertThat(result).extractingJsonPathNumberValue("$.bookingId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookingId").isEqualTo(1);
        assertThat(result).extractingJsonPathBooleanValue("$.approved").isEqualTo(true);
    }

    @Test
    void testBookingResponse() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        CommentResponse commentResponse = CommentResponse.builder()
                .id(1L)
                .text("comment")
                .itemId(1L)
                .authorName("author_name")
                .created("2025-04-08 12:30")
                .build();

        List<CommentResponse> comments = new ArrayList<>();
        comments.add(commentResponse);

        ItemResponse itemResponse = ItemResponse.builder()
                .id(1L)
                .name("item1")
                .description("item1_description")
                .available(true)
                .request(1L)
                .comments(comments)
                .build();

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(1L)
                .start("2025-04-08 12:30")
                .end("2025-05-08 12:30")
                .item(itemResponse)
                .booker(userResponse)
                .status(BookingStatus.APPROVED)
                .build();

        JsonContent<BookingResponse> result = bookingResponseJacksonTester.write(bookingResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-04-08 12:30");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-05-08 12:30");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item1");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("item1_description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.request").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.comments.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.comments.[0].text").isEqualTo("comment");
        assertThat(result).extractingJsonPathNumberValue("$.item.comments.[0].itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.comments.[0].authorName").isEqualTo("author_name");
        assertThat(result).extractingJsonPathStringValue("$.item.comments.[0].created").isEqualTo("2025-04-08 12:30");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("user1@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(String.valueOf(BookingStatus.APPROVED));
    }

    @Test
    void testBookingShortResponse() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        BookingShortResponse bookingShortResponse = BookingShortResponse.builder()
                .id(1L)
                .start("2025-04-08 12:30")
                .end("2025-05-08 12:30")
                .booker(userResponse)
                .build();

        JsonContent<BookingShortResponse> result = bookingShortResponseJacksonTester.write(bookingShortResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-04-08 12:30");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-05-08 12:30");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("user1@mail.ru");
    }

    @Test
    void testCreateBookingRequest() throws Exception {
        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .start(LocalDateTime.of(2025, Month.MAY, 8, 12, 30))
                .end(LocalDateTime.of(2025, Month.MAY, 10, 12, 30))
                .itemId(1L)
                .build();

        JsonContent<CreateBookingRequest> result = createBookingRequestJacksonTester.write(createBookingRequest);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-05-08T12:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-05-10T12:30:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}