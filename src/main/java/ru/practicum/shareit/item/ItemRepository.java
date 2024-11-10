package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item createItem(Item item);

    List<Item> getAllItemsByUserId(long userId);

    Optional<Item> getItemById(long itemId);

    List<Item> searchItems(String text);

    Item updateItem(long itemId, Item newItem);

    void deleteItemById(long itemId);
}