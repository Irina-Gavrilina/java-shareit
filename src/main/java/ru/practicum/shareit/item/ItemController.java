package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос POST на создание вещи {}", itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Поступил запрос GET на получение списка всех вещей пользователя");
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") long itemId) {
        log.info("Поступил запрос GET на получение данных о вещи с id = {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        return itemService.searchItems(text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable("itemId") long itemId,
                              @RequestBody ItemDto newItemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос PATCH на редактирование вещи {}", newItemDto);
        return itemService.updateItem(itemId, newItemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(long itemId) {
        log.info("Получен запрос DELETE на удаление вещи c id = {}", itemId);
        itemService.deleteItemById(itemId);
    }
}