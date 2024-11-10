package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import java.util.*;

@Repository
@Slf4j
public class InMemoryItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAllItemsByUserId(long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .toList();
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values()
                .stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .toList();
    }

    @Override
    public Item updateItem(long itemId, Item newItem) {
        if (!items.containsKey(itemId)) {
            log.error("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException(String.format("Вещи с id = %d нет в базе", itemId));
        }

        Item oldItem = items.get(itemId);

        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        return oldItem;
    }

    @Override
    public void deleteItemById(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException(String.format("Не найдена вещь с id = %d для удаления", itemId));
        }
        items.remove(itemId);
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(itemId -> itemId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}