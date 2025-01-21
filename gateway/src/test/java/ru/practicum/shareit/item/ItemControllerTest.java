package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.commentDto.CreateCommentRequest;
import ru.practicum.shareit.item.itemDto.CreateItemRequest;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    @Test
    public void createItemWithBlankNameFieldTest() throws Exception {
        CreateItemRequest createItemRequest = new CreateItemRequest("", "description", true, null);

        mvc.perform(MockMvcRequestBuilders.post("/items")
                        .content(mapper.writeValueAsString(createItemRequest))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Произошла непредвиденная ошибка")));

        verify(itemClient, never()).createItem(anyLong(), any(CreateItemRequest.class));
    }

    @Test
    public void createItemWithBlankDescriptionFieldTest() throws Exception {
        CreateItemRequest createItemRequest = new CreateItemRequest("item_request1", "", true, null);

        mvc.perform(MockMvcRequestBuilders.post("/items")
                        .content(mapper.writeValueAsString(createItemRequest))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Произошла непредвиденная ошибка")));

        verify(itemClient, never()).createItem(anyLong(), any(CreateItemRequest.class));
    }

    @Test
    public void createItemWithBlankAvailableFieldTest() throws Exception {
        CreateItemRequest createItemRequest = new CreateItemRequest("item_request1", "description", null, null);

        mvc.perform(MockMvcRequestBuilders.post("/items")
                        .content(mapper.writeValueAsString(createItemRequest))
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Произошла непредвиденная ошибка")));

        verify(itemClient, never()).createItem(anyLong(), any(CreateItemRequest.class));
    }

    @Test
    public void addCommentWithBlankTextFieldTest() throws Exception {
        CreateCommentRequest createCommentRequest = new CreateCommentRequest("");

        mvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(createCommentRequest))
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Произошла непредвиденная ошибка")));

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any(CreateCommentRequest.class));
    }
}