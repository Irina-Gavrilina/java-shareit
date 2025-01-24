package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemInfo;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestCreateDto request, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId)));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(request, user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDtoWithItemInfo> getAllItemRequestsByUserId(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId)));

        Map<Long, ItemRequest> itemRequestMap = itemRequestRepository.getAllItemRequestsByUserIdOrderByStartDesc(userId)
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        Map<ItemRequest, List<Item>> itemMap = itemRepository.findAllItemsByRequestIdIn(itemRequestMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(Item::getRequest));

        return itemRequestMap.values()
                .stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDtoWithItemInfo(itemRequest,
                        itemMap.getOrDefault(itemRequest, Collections.emptyList())))
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(long userId) {
        List<ItemRequest> itemRequestList = itemRequestRepository.findAll(Sort.by(Sort.Direction.DESC, "created"));
        return itemRequestList.stream()
                .filter(ItemRequest -> ItemRequest.getRequester().getId() != userId)
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDtoWithItemInfo getItemRequestById(long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запроса вещи с id = %d нет в базе", requestId)));

        Map<ItemRequest, List<Item>> itemMap = itemRepository.findAllItemsByRequestId(itemRequest.getId())
                .stream()
                .collect(Collectors.groupingBy(Item::getRequest));

        return ItemRequestMapper.toItemRequestDtoWithItemInfo(itemRequest, itemMap.getOrDefault(itemRequest,
                Collections.emptyList()));
    }
}