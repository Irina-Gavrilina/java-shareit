package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import java.util.List;
import static ru.practicum.shareit.constants.Constants.FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requester(request.getRequester())
                .created(FORMATTER.format(request.getCreated()))
                .build();
    }

    public static ItemRequestDtoWithItemInfo toItemRequestDtoWithItemInfo(ItemRequest request, List<Item> items) {
        return ItemRequestDtoWithItemInfo.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(FORMATTER.format(request.getCreated()))
                .items(ItemMapper.toListOfItemInfoForItemRequest(items))
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestCreateDto request, User user) {
        return ItemRequest.builder()
                .description(request.getDescription())
                .requester(user)
                .created(LocalDateTime.now())
                .build();
    }
}