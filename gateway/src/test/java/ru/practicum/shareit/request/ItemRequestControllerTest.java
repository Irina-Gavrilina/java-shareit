package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private MockMvc mvc;

    @Test
    public void createItemRequestWithBlankDescriptionFieldTest() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("");

        mvc.perform(MockMvcRequestBuilders.post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Произошла непредвиденная ошибка")));

        verify(itemRequestClient, never()).createItemRequest(anyLong(), any(ItemRequestCreateDto.class));
    }
}