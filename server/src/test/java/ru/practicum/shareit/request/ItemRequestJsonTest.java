package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.itemDto.ItemInfoForItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemInfo;
import ru.practicum.shareit.user.User;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestJsonTest {

    final JacksonTester<ItemRequestCreateDto> itemRequestCreateDtoJacksonTester;
    final JacksonTester<ItemRequestDto> itemRequestDtoJacksonTester;
    final JacksonTester<ItemRequestDtoWithItemInfo> itemRequestDtoWithItemInfoJacksonTester;

    @Test
    void testItemRequestCreateDto() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("description");

        JsonContent<ItemRequestCreateDto> result = itemRequestCreateDtoJacksonTester.write(itemRequestCreateDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    void testItemRequestDto() throws Exception {
        User user = User.builder()
                .name("user1")
                .email("user1@mail.ru")
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requester(user)
                .created("2025-04-08 12:30")
                .build();

        JsonContent<ItemRequestDto> result = itemRequestDtoJacksonTester.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.requester.name").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-04-08 12:30");
    }

    @Test
    void testItemRequestDtoWithItemInfo() throws Exception {
        ItemInfoForItemRequest itemInfoForItemRequest = ItemInfoForItemRequest.builder()
                .id(1L)
                .name("name")
                .ownerId(1L)
                .build();

        List<ItemInfoForItemRequest> items = new ArrayList<>();
        items.add(itemInfoForItemRequest);

        ItemRequestDtoWithItemInfo itemRequestDtoWithItemInfo = ItemRequestDtoWithItemInfo.builder()
                .id(1L)
                .description("description")
                .created("2025-04-08 12:30")
                .items(items)
                .build();

        JsonContent<ItemRequestDtoWithItemInfo> result = itemRequestDtoWithItemInfoJacksonTester.write(itemRequestDtoWithItemInfo);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-04-08 12:30");
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items.[0].name").isEqualTo("name");
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].ownerId").isEqualTo(1);
    }
}