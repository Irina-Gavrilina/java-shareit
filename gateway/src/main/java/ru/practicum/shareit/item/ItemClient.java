package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.commentDto.CreateCommentRequest;
import ru.practicum.shareit.item.itemDto.CreateItemRequest;
import ru.practicum.shareit.item.itemDto.UpdateItemRequest;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long userId, CreateItemRequest request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> getAllItemsByUserId(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemById(long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> searchItems(String searchText) {
        Map<String, Object> parameters = Map.of("searchText", searchText);
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, UpdateItemRequest request) {
        return patch("/" + itemId, userId, request);
    }

    public ResponseEntity<Object> deleteItemById(long itemId) {
        return delete("/" + itemId);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CreateCommentRequest request) {
        return post("/" + itemId + "/comment", userId, request);
    }
}