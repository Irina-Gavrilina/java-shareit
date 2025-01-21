package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.commentDto.CreateCommentRequest;
import ru.practicum.shareit.item.itemDto.CreateItemRequest;
import ru.practicum.shareit.item.itemDto.UpdateItemRequest;
import static ru.practicum.shareit.constants.Constants.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) long userId,
                                             @RequestBody @Valid CreateItemRequest request) {
        log.info("Получен запрос POST на создание вещи {}", request);
        return itemClient.createItem(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Поступил запрос GET на получение списка всех вещей пользователя");
        return itemClient.getAllItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable("itemId") long itemId) {
        log.info("Поступил запрос GET на получение данных о вещи с id = {}", itemId);
        return itemClient.getItemById(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String searchText) {
        return itemClient.searchItems(searchText);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) long userId,
                                   @PathVariable("itemId") long itemId,
                                   @RequestBody @Valid UpdateItemRequest request) {
        log.info("Получен запрос PATCH на редактирование вещи {}", request);
        return itemClient.updateItem(userId, itemId, request);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItemById(@PathVariable("itemId") long itemId) {
        log.info("Получен запрос DELETE на удаление вещи c id = {}", itemId);
        return itemClient.deleteItemById(itemId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                      @PathVariable("itemId") long itemId,
                                      @RequestBody @Valid CreateCommentRequest request) {
        log.info("Получен запрос POST на добавление нового комментария {}", request);
        return itemClient.addComment(userId, itemId, request);
    }
}