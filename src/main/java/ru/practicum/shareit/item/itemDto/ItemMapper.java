package ru.practicum.shareit.item.itemDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortResponse;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.commentDto.CommentMapper;
import ru.practicum.shareit.user.User;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemResponse toItemResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static ItemResponse toItemResponse(Item item, List<Comment> comments) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .comments(comments.stream().map(CommentMapper::toCommentResponse).toList())
                .build();
    }

    public static ItemInfoResponse toItemInfoResponse(Item item, Booking lastBookingDate, Booking nextBookingDate,
                                                      List<Comment> comments) {
        BookingShortResponse lastBooking = null;
        BookingShortResponse nextBooking = null;

        if (lastBookingDate != null) {
            lastBooking = BookingMapper.toBookingShortResponse(lastBookingDate);
        }

        if (nextBookingDate != null) {
            nextBooking = BookingMapper.toBookingShortResponse(nextBookingDate);
        }

        return ItemInfoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments.stream().map(CommentMapper::toCommentResponse).toList())
                .build();
    }

    public static List<ItemResponse> toListOfItemsResponse(List<Item> items) {
        if (items == null) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(ItemMapper::toItemResponse)
                .toList();
    }

    public static Item toItem(CreateItemRequest request, User user) {
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .available(request.getAvailable())
                .owner(user)
                .build();
    }
}