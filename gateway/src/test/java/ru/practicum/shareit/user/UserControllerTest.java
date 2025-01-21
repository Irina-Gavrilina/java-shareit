package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @Autowired
    private MockMvc mvc;

    @Test
    public void createUserWithBlankNameFieldTest() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("", "user1@mail.ru");

        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Произошла непредвиденная ошибка")));

        verify(userClient, never()).createUser(any(CreateUserRequest.class));
    }

    @Test
    public void createUserWithBlankEmailFieldTest() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("user1", "");

        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Произошла непредвиденная ошибка")));

        verify(userClient, never()).createUser(any(CreateUserRequest.class));
    }

    @Test
    public void createUserWithIncorrectEmailTest() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("user1", "user1mail.ru");

        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Произошла непредвиденная ошибка")));

        verify(userClient, never()).createUser(any(CreateUserRequest.class));
    }

    @Test
    public void createUserWhenEmailIsAlreadyExistTest() throws Exception {
        String existingEmail = "existingEmail@mail.ru";
        CreateUserRequest createUserRequest = new CreateUserRequest("user1", existingEmail);

        given(userClient.createUser(createUserRequest))
                .willThrow(new RuntimeException("Пользователь с таким email уже существует в базе"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Произошла непредвиденная ошибка")));

        verify(userClient).createUser(createUserRequest);
    }
}