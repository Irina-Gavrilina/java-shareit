package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import static ru.practicum.shareit.constants.Constants.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                                    @RequestBody @Valid ItemRequestCreateDto request) {
        log.info("Получен запрос POST на создание запроса вещи {}", request);
        return itemRequestClient.createItemRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Поступил запрос GET на получение списка всех запросов вещей пользователя с id = {}", userId);
        return itemRequestClient.getAllItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Поступил запрос GET на получение списка всех существующих запросов вещей");
        return itemRequestClient.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable("requestId") long requestId) {
        log.info("Поступил запрос GET на получение запроса вещи id = {}", requestId);
        return itemRequestClient.getItemRequestById(requestId);
    }
}