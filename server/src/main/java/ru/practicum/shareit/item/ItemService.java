package ru.practicum.shareit.item;

import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequest;
import ru.practicum.shareit.item.itemDto.CreateItemRequest;
import ru.practicum.shareit.item.itemDto.ItemInfoResponse;
import ru.practicum.shareit.item.itemDto.ItemResponse;
import ru.practicum.shareit.item.itemDto.UpdateItemRequest;
import java.util.List;

public interface ItemService {
    ItemResponse createItem(CreateItemRequest request, long userId);

    List<ItemInfoResponse> getAllItemsByUserId(long userId);

    ItemInfoResponse getItemById(long itemId);

    List<ItemResponse> searchItems(String text);

    ItemResponse updateItem(long itemId, UpdateItemRequest request, long userId);

    void deleteItemById(long itemId);

    CommentResponse addComment(long itemId, CreateCommentRequest request, long userId);
}