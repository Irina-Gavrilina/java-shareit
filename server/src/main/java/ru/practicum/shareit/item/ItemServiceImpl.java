package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableBookingException;
import ru.practicum.shareit.item.commentDto.CommentMapper;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequest;
import ru.practicum.shareit.item.itemDto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemResponse createItem(CreateItemRequest request, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(
                "Пользователя с id = %d нет в базе", userId)));
        Long requestId = request.getRequestId();

        if (requestId != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                    new NotFoundException(String.format("Запроса вещи с id = %d нет в базе", requestId)));
            Item item = ItemMapper.toItem(request, itemRequest, user);
            return ItemMapper.toItemResponse(itemRepository.save(item));
        }
        Item item = ItemMapper.toItem(request, null, user);
        return ItemMapper.toItemResponse(itemRepository.save(item));
    }

    @Override
    public List<ItemInfoResponse> getAllItemsByUserId(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(
                "Пользователя с id = %d нет в базе", userId)));

        Map<Long, Item> itemMap = itemRepository.findByOwnerId(user.getId())
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Item, List<Booking>> bookingMap = bookingRepository.findByItemIdIn(itemMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(Booking::getItem));

        return itemMap.values()
                .stream()
                .map(item -> makeItemInfoResponse(item, bookingMap
                        .getOrDefault(item, Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemInfoResponse getItemById(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(String.format("Вещи с id " +
                "= %d нет в базе", itemId)));
        List<Comment> comments = commentRepository.findByItemWithAuthor(item);
        return ItemMapper.toItemInfoResponse(item, null, null, comments);
    }

    @Override
    public List<ItemResponse> searchItems(String searchText) {
        List<Item> listOfItems = itemRepository.searchItems(searchText);
        return ItemMapper.toListOfItemsResponse(listOfItems);
    }

    @Override
    @Transactional
    public ItemResponse updateItem(long itemId, UpdateItemRequest request, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(
                "Пользователя с id = %d нет в базе", userId)));
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(String.format(
                "Вещи с id %d нет в базе", itemId)));

        if (!oldItem.getOwner().equals(user)) {
            log.error("Пользователь с id = {} не является владельцем данной вещи", user.getId());
            throw new ForbiddenException("Редактировать вещь может только её владелец");
        }
        if (request.hasName()) {
            oldItem.setName(request.getName());
        }
        if (request.hasDescription()) {
            oldItem.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            oldItem.setAvailable(request.getAvailable());
        }
        return ItemMapper.toItemResponse(itemRepository.save(oldItem));
    }

    @Override
    @Transactional
    public void deleteItemById(long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentResponse addComment(long itemId, CreateCommentRequest request, long userId) {
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(
                "Пользователя с id = %d нет в базе", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(String.format(
                "Вещи с id %d нет в базе", itemId)));
        Optional<Booking> booking = bookingRepository.getPastBookingByBookerIdAndItemId(item.getId(), author.getId(),
                LocalDateTime.now());
        if (booking.isPresent()) {
            Comment comment = CommentMapper.toComment(request, item, author);
            return CommentMapper.toCommentResponse(commentRepository.save(comment));
        } else {
            throw new UnavailableBookingException(String.format("Пользователь с id = %d не бронировал вещь с id = %d," +
                    " либо бронирование ещё не завершено", author.getId(), item.getId()));
        }
    }

    private ItemInfoResponse makeItemInfoResponse(Item item, List<Booking> bookings) {

        Booking lastBookingDate = bookings.stream()
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart)).orElse(null);

        Booking nextBookingDate = bookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);

        List<Comment> comments = commentRepository.findByItemWithAuthor(item);
        return ItemMapper.toItemInfoResponse(item, lastBookingDate, nextBookingDate, comments);
    }
}