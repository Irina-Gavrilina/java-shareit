package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequest;
import ru.practicum.shareit.item.itemDto.CreateItemRequest;
import ru.practicum.shareit.item.itemDto.ItemInfoResponse;
import ru.practicum.shareit.item.itemDto.ItemResponse;
import ru.practicum.shareit.item.itemDto.UpdateItemRequest;
import ru.practicum.shareit.user.User;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.shareit.constants.Constants.FORMATTER;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void createItemTest() throws Exception {
        User user = new User(1L, "user1", "user1@mail.ru");
        CreateItemRequest createItemRequest = new CreateItemRequest("item1", "item1_description", true, null);
        ItemResponse itemResponse = new ItemResponse(1L, "item1", "item1_description", true, null, null);

        when(itemService.createItem(createItemRequest, user.getId()))
                .thenReturn(itemResponse);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(createItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())))
                .andExpect(jsonPath("$.request", is(itemResponse.getRequest())))
                .andExpect(jsonPath("$.comments", is(itemResponse.getComments())));

        verify(itemService, Mockito.times(1)).createItem(createItemRequest, user.getId());
    }

    @Test
    public void getAllItemsByUserIdTest() throws Exception {
        User owner = new User(1L, "user1", "user1@mail.ru");
        ItemInfoResponse firstItemInfoResponse = new ItemInfoResponse(1L, "item1", "item1_description", true, null,
                null, null, null);
        ItemInfoResponse secondItemInfoResponse = new ItemInfoResponse(2L, "item2", "item2_description", true, null,
                null, null, null);

        when(itemService.getAllItemsByUserId(owner.getId()))
                .thenReturn(List.of(firstItemInfoResponse, secondItemInfoResponse));

        mvc.perform(get("/items", owner.getId())
                        .header("X-Sharer-User-Id", owner.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("item1"))
                .andExpect(jsonPath("$[0].description").value("item1_description"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("item2"))
                .andExpect(jsonPath("$[1].description").value("item2_description"))
                .andExpect(jsonPath("$[1].available").value(true));

        verify(itemService, Mockito.times(1)).getAllItemsByUserId(owner.getId());
    }

    @Test
    public void getItemByIdTest() throws Exception {
        User owner = new User(1L, "user1", "user1@mail.ru");
        Item item = new Item(1L, "item1", "item1_description", true, owner, null);
        ItemInfoResponse itemInfoResponse = new ItemInfoResponse(1L, "item1", "item1_description", true, null,
                null, null, null);

        when(itemService.getItemById(item.getId()))
                .thenReturn(itemInfoResponse);

        mvc.perform(get("/items/{itemId}", item.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("item1"))
                .andExpect(jsonPath("$.description").value("item1_description"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, Mockito.times(1)).getItemById(item.getId());
    }

    @Test
    public void searchItemsTest() throws Exception {
        String searchText = "item";
        ItemResponse firstItemResponse = new ItemResponse(1L, "item1", "item1_description", true, null, null);
        ItemResponse secondItemResponse = new ItemResponse(2L, "item2", "item2_description", true, null, null);

        when(itemService.searchItems(anyString()))
                .thenReturn(List.of(firstItemResponse, secondItemResponse));

        mvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(firstItemResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(firstItemResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(firstItemResponse.getDescription())))
                .andExpect(jsonPath("$[1].id", is(secondItemResponse.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(secondItemResponse.getName())))
                .andExpect(jsonPath("$[1].description", is(secondItemResponse.getDescription())));

        verify(itemService, Mockito.times(1)).searchItems(anyString());
    }

    @Test
    public void updateItemTest() throws Exception {
        User owner = new User(1L, "user1", "user1@mail.ru");
        Item item = new Item(1L, "item1", "item1_description", true, owner, null);
        UpdateItemRequest updateItemRequest = new UpdateItemRequest(1L, "item2", "item2_description", true);
        ItemResponse itemResponse = new ItemResponse(1L, "item2", "item2_description", true, null, null);

        when(itemService.updateItem(item.getId(), updateItemRequest, owner.getId()))
                .thenReturn(itemResponse);

        mvc.perform(patch("/items/{itemId}", item.getId())
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", owner.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())))
                .andExpect(jsonPath("$.request", is(itemResponse.getRequest())))
                .andExpect(jsonPath("$.comments", is(itemResponse.getComments())));

        verify(itemService, Mockito.times(1)).updateItem(item.getId(), updateItemRequest, owner.getId());
    }

    @Test
    public void deleteItemByIdTest() throws Exception {
        User owner = new User(1L, "user1", "user1@mail.ru");
        Item item = new Item(1L, "item1", "item1_description", true, owner, null);

        doNothing().when(itemService).deleteItemById(item.getId());

        mvc.perform(delete("/items/{itemId}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, Mockito.times(1)).deleteItemById(item.getId());
    }

    @Test
    public void addCommentTest() throws Exception {
        User owner = new User(1L, "user1", "user1@mail.ru");
        User booker = new User(2L, "user2", "user2@mail.ru");
        Item item = new Item(1L, "item1", "item1_description", true, owner, null);
        CreateCommentRequest createCommentRequest = new CreateCommentRequest("Comment");
        CommentResponse commentResponse = new CommentResponse(1L, "Comment", item.getId(), "user2",
                FORMATTER.format(LocalDateTime.now()));

        when(itemService.addComment(item.getId(), createCommentRequest, booker.getId()))
                .thenReturn(commentResponse);

        mvc.perform(post("/items/{itemId}/comment", item.getId())
                        .content(mapper.writeValueAsString(createCommentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentResponse.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponse.getText())))
                .andExpect(jsonPath("$.itemId", is(commentResponse.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentResponse.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentResponse.getCreated())));

        verify(itemService, Mockito.times(1)).addComment(item.getId(), createCommentRequest, booker.getId());
    }
}