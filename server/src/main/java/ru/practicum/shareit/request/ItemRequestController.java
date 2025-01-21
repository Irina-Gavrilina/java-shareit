package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemInfo;
import java.util.List;
import static ru.practicum.shareit.constants.Constants.USER_ID_HEADER;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                            @RequestBody ItemRequestCreateDto request) {
        log.info("Получен запрос POST на создание запроса вещи {}", request);
        return itemRequestService.createItemRequest(request, userId);
    }

    @GetMapping
    public List<ItemRequestDtoWithItemInfo> getAllItemRequestsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Поступил запрос GET на получение списка всех запросов вещей пользователя с id = {}", userId);
        return itemRequestService.getAllItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Поступил запрос GET на получение списка всех существующих запросов вещей");
        return itemRequestService.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItemInfo getItemRequestById(@PathVariable("requestId") long requestId) {
        log.info("Поступил запрос GET на получение запроса вещи id = {}", requestId);
        return itemRequestService.getItemRequestById(requestId);
    }
}