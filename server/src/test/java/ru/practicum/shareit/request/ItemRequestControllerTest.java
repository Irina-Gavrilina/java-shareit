package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItemInfo;
import ru.practicum.shareit.user.User;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.practicum.shareit.constants.Constants.FORMATTER;
import static ru.practicum.shareit.constants.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void createItemRequestTest() throws Exception {
        User requester = new User(1L, "user1", "user1@mail.ru");
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("item_request1_description");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "item_request1_description", requester,
                FORMATTER.format(LocalDateTime.now()));

        when(itemRequestService.createItemRequest(itemRequestCreateDto, requester.getId()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, requester.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester", is(itemRequestDto.getRequester()), User.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated())));

        verify(itemRequestService, Mockito.times(1)).createItemRequest(itemRequestCreateDto, requester.getId());
    }

    @Test
    public void getAllItemRequestsByUserIdTest() throws Exception {
        User requester = new User(1L, "user1", "user1@mail.ru");

        ItemRequestDtoWithItemInfo itemRequestDtoWithItemInfo = new ItemRequestDtoWithItemInfo(1L, "description",
                FORMATTER.format(LocalDateTime.now()), null);

        when(itemRequestService.getAllItemRequestsByUserId(requester.getId()))
                .thenReturn(List.of(itemRequestDtoWithItemInfo));

        mvc.perform(get("/requests", requester.getId())
                        .header(USER_ID_HEADER, requester.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("description"))
                .andExpect(jsonPath("$[0].created").value(itemRequestDtoWithItemInfo.getCreated()));

        verify(itemRequestService, Mockito.times(1)).getAllItemRequestsByUserId(requester.getId());
    }

    @Test
    public void getAllItemRequestsTest() throws Exception {
        User requester = new User(1L, "user1", "user1@mail.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", requester,
                FORMATTER.format(LocalDateTime.now()));

        when(itemRequestService.getAllItemRequests(requester.getId()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all", requester.getId())
                        .header(USER_ID_HEADER, requester.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("description"))
                .andExpect(jsonPath("$[0].requester").value(requester))
                .andExpect(jsonPath("$[0].created").value(itemRequestDto.getCreated()));

        verify(itemRequestService, Mockito.times(1)).getAllItemRequests(requester.getId());
    }

    @Test
    public void getItemRequestByIdTest() throws Exception {
        User requester = new User(1L, "user1", "user1@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, LocalDateTime.now());
        ItemRequestDtoWithItemInfo itemRequestDtoWithItemInfo = new ItemRequestDtoWithItemInfo(1L, "description",
                FORMATTER.format(itemRequest.getCreated()), null);

        when(itemRequestService.getItemRequestById(itemRequest.getId()))
                .thenReturn(itemRequestDtoWithItemInfo);

        mvc.perform(get("/requests/{requestId}", itemRequest.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.created").value(itemRequestDtoWithItemInfo.getCreated()));

        verify(itemRequestService, Mockito.times(1)).getItemRequestById(itemRequest.getId());
    }
}