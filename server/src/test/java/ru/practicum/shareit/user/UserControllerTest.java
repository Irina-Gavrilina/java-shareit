package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void createUserTest() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("user1", "user1@mail.ru");
        UserResponse userResponse = new UserResponse(1L, "user1", "user1@mail.ru");

        when(userService.createUser(createUserRequest))
                .thenReturn(userResponse);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));

        verify(userService, Mockito.times(1)).createUser(createUserRequest);
    }

    @Test
    public void getAllUsersTest() throws Exception {
        when(userService.getAllUsers())
                .thenAnswer(invocationOnMock -> {
                    List<UserResponse> users = new ArrayList<>();
                    users.add(new UserResponse(1L, "user1", "user1@mail.ru"));
                    users.add(new UserResponse(2L, "user2", "user2@mail.ru"));
                    return users;
                });

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("user1"))
                .andExpect(jsonPath("$[0].email").value("user1@mail.ru"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("user2"))
                .andExpect(jsonPath("$[1].email").value("user2@mail.ru"));

        verify(userService, Mockito.times(1)).getAllUsers();
    }

    @Test
    public void getUserByIdTest() throws Exception {
        User user = new User(1L, "user1", "user1@mail.ru");
        UserResponse userResponse = new UserResponse(1L, "user1", "user1@mail.ru");

        when(userService.getUserById(user.getId()))
                .thenReturn(userResponse);

        mvc.perform(get("/users/{userId}", userResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));

        verify(userService, Mockito.times(1)).getUserById(user.getId());
    }

    @Test
    public void updateUserTest() throws Exception {
        User user = new User(1L, "user1", "user1@mail.ru");
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(1L, "user2", "user2@mail.ru");
        UserResponse userResponse = new UserResponse(1L, "user2", "user2@mail.ru");

        when(userService.updateUser(user.getId(), updateUserRequest))
                .thenReturn(userResponse);

        mvc.perform(patch("/users/{userId}", user.getId())
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));

        verify(userService, Mockito.times(1)).updateUser(user.getId(), updateUserRequest);
    }

    @Test
    public void deleteUserByIdTest() throws Exception {
        User user = new User(1L, "user1", "user1@mail.ru");

        doNothing().when(userService).deleteUserById(anyLong());

        mvc.perform(delete("/users/{userId}", user.getId()))
                .andExpect(status().isOk());

        verify(userService, Mockito.times(1)).deleteUserById(user.getId());
    }
}