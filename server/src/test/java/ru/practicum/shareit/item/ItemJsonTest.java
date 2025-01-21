package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingShortResponse;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequest;
import ru.practicum.shareit.item.itemDto.*;
import ru.practicum.shareit.user.dto.UserResponse;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemJsonTest {

    final JacksonTester<CreateItemRequest> createItemRequestJacksonTester;
    final JacksonTester<ItemInfoForItemRequest> itemInfoForItemRequestJacksonTester;
    final JacksonTester<ItemInfoResponse> itemInfoResponseJacksonTester;
    final JacksonTester<ItemResponse> itemResponseJacksonTester;
    final JacksonTester<UpdateItemRequest> updateItemRequestJacksonTester;
    final JacksonTester<CommentResponse> commentResponseJacksonTester;
    final JacksonTester<CreateCommentRequest> createCommentRequestJacksonTester;

    @Test
    void testCreateUserRequest() throws Exception {
        CreateItemRequest createItemRequest = CreateItemRequest.builder()
                .name("item1")
                .description("item1_description")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<CreateItemRequest> result = createItemRequestJacksonTester.write(createItemRequest);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item1_description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testItemInfoForItemRequest() throws Exception {
        ItemInfoForItemRequest itemInfoForItemRequest = ItemInfoForItemRequest.builder()
                .id(1L)
                .name("name")
                .ownerId(1L)
                .build();

        JsonContent<ItemInfoForItemRequest> result = itemInfoForItemRequestJacksonTester.write(itemInfoForItemRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
    }

    @Test
    void testItemInfoResponse() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        BookingShortResponse lastBooking = BookingShortResponse.builder()
                .id(1L)
                .start("2025-04-08 12:30")
                .end("2025-06-08 12:30")
                .booker(userResponse)
                .build();

        ItemInfoResponse itemInfoResponse = ItemInfoResponse.builder()
                .id(1L)
                .name("item1")
                .description("item1_description")
                .available(true)
                .request(2L)
                .lastBooking(lastBooking)
                .nextBooking(null)
                .comments(null)
                .build();

        JsonContent<ItemInfoResponse> result = itemInfoResponseJacksonTester.write(itemInfoResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item1_description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.request").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2025-04-08 12:30");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2025-06-08 12:30");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.booker.name").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.booker.email").isEqualTo("user1@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isNull();
        assertThat(result).extractingJsonPathStringValue("$.comments").isNull();
    }

    @Test
    void testItemResponse() throws Exception {
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
                .available(false)
                .request(1L)
                .comments(comments)
                .build();

        JsonContent<ItemResponse> result = itemResponseJacksonTester.write(itemResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item1_description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(false);
        assertThat(result).extractingJsonPathNumberValue("$.request").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].text").isEqualTo("comment");
        assertThat(result).extractingJsonPathNumberValue("$.comments.[0].itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].authorName").isEqualTo("author_name");
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].created").isEqualTo("2025-04-08 12:30");
    }

    @Test
    void testUpdateItemRequest() throws Exception {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        JsonContent<UpdateItemRequest> result = updateItemRequestJacksonTester.write(updateItemRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void testCommentResponse() throws Exception {
        CommentResponse commentResponse = CommentResponse.builder()
                .id(1L)
                .text("comment")
                .itemId(1L)
                .authorName("author_name")
                .created("2025-04-08 12:30")
                .build();

        JsonContent<CommentResponse> result = commentResponseJacksonTester.write(commentResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("author_name");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-04-08 12:30");
    }

    @Test
    void testCreateCommentRequest() throws Exception {
        CreateCommentRequest createCommentRequest = new CreateCommentRequest("comment");

        JsonContent<CreateCommentRequest> result = createCommentRequestJacksonTester.write(createCommentRequest);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment");
    }
}