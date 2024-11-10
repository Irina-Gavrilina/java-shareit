package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    List<ItemDto> getAllItemsByUserId(long userId);

    ItemDto getItemById(long itemId);

    List<ItemDto> searchItems(String text);

    ItemDto updateItem(long itemId, ItemDto newItemDto, long userId);

    void deleteItemById(long itemId);
}