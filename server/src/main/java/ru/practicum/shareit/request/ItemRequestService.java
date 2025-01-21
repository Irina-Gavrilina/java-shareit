package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemInfo;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestCreateDto request, long userId);

    List<ItemRequestDtoWithItemInfo> getAllItemRequestsByUserId(long userId);

    List<ItemRequestDto> getAllItemRequests(long userId);

    ItemRequestDtoWithItemInfo getItemRequestById(long requestId);
}
