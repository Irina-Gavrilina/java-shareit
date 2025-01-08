package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequest;
import ru.practicum.shareit.item.itemDto.CreateItemRequest;
import ru.practicum.shareit.item.itemDto.ItemInfoResponse;
import ru.practicum.shareit.item.itemDto.ItemResponse;
import ru.practicum.shareit.item.itemDto.UpdateItemRequest;
import java.util.List;
import static ru.practicum.shareit.constants.Constants.USER_ID_HEADER;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@Valid @RequestBody CreateItemRequest request,
                                   @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Получен запрос POST на создание вещи {}", request);
        return itemService.createItem(request, userId);
    }

    @GetMapping
    public List<ItemInfoResponse> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Поступил запрос GET на получение списка всех вещей пользователя");
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemInfoResponse getItemById(@PathVariable("itemId") long itemId) {
        log.info("Поступил запрос GET на получение данных о вещи с id = {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchItems(@RequestParam("text") String searchText) {
        return itemService.searchItems(searchText);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(@PathVariable("itemId") long itemId,
                                   @Valid @RequestBody UpdateItemRequest request,
                                   @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Получен запрос PATCH на редактирование вещи {}", request);
        return itemService.updateItem(itemId, request, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(long itemId) {
        log.info("Получен запрос DELETE на удаление вещи c id = {}", itemId);
        itemService.deleteItemById(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse addComment(@PathVariable("itemId") long itemId,
                                      @Valid @RequestBody CreateCommentRequest request,
                                      @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Получен запрос POST на добавление нового комментария {}", request);
        return itemService.addComment(itemId, request, userId);
    }
}