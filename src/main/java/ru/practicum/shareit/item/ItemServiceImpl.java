package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        Optional<User> optUser = userRepository.getUserById(userId);
        if (optUser.isPresent()) {
            User user = optUser.get();
            Item item = ItemMapper.toItem(itemDto, user);
            return ItemMapper.toItemDto(itemRepository.createItem(item));
        }
        log.error("Пользователь с id = {} не найден", userId);
        throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId) {
        return ItemMapper.toListOfItemsDto(itemRepository.getAllItemsByUserId(userId));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Optional<Item> optItem = itemRepository.getItemById(itemId);
        if (optItem.isPresent()) {
            Item item = optItem.get();
            return ItemMapper.toItemDto(item);
        }
        log.error("Вещь с id = {} не найдена", itemId);
        throw new NotFoundException(String.format("Вещи с id = %d нет в базе", itemId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return ItemMapper.toListOfItemsDto(itemRepository.searchItems(text));
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDto newItemDto, long userId) {
        Optional<User> optUser = userRepository.getUserById(userId);
        if (optUser.isEmpty()) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
        }
        Optional<Item> optItem = itemRepository.getItemById(itemId);
        if (optItem.isEmpty()) {
            log.error("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException(String.format("Вещи с id = %d нет в базе", itemId));
        }
        User user = optUser.get();
        Item item = ItemMapper.toItem(newItemDto, user);
        if (!item.getOwner().equals(user)) {
            log.error("Пользователь с id = {} не является владельцем данной вещи", user.getId());
            throw new ForbiddenException("Редактировать вещь может только её владелец");
        }
        return ItemMapper.toItemDto(itemRepository.updateItem(itemId, item));
    }

    @Override
    public void deleteItemById(long itemId) {
        itemRepository.deleteItemById(itemId);
    }
}