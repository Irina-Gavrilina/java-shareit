package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void createUserTest() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .name("user1")
                .email("user1@mail.ru")
                .build();

        User user = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponse result = userService.createUser(createUserRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void getAllUsersTest() {
        User firstUser = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        User secondUser = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        UserResponse firstUserResponse = UserResponse.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        UserResponse secondUserResponse = UserResponse.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        when(userRepository.findAll())
                .thenReturn(List.of(firstUser, secondUser));

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(List.of(firstUserResponse, secondUserResponse), result);
        verify(userRepository).findAll();
    }

    @Test
    public void getAllUsersWhenThereAreNoUsersTest() {
        when(userRepository.findAll())
                .thenReturn(null);

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(List.of(), result);
        verify(userRepository).findAll();
    }

    @Test
    public void getUserByIdWhenUserExistsTest() {
        User user = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UserResponse result = userService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository).findById(anyLong());
    }

    @Test
    public void getUserByIdWhenUserNotFoundTest() {
        long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(userId)));
        verify(userRepository, Mockito.times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateUserWhenUserExistsTest() {
        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .id(1L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        User user = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponse result = userService.updateUser(user.getId(), updateUserRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository).findById(user.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void updateUserWhenUserExistsWithEmptyNameTest() {
        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .id(1L)
                .email("user2@mail.ru")
                .build();

        User user = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("user1")
                .email("user2@mail.ru")
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponse result = userService.updateUser(user.getId(), updateUserRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository).findById(user.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void updateUserWhenUserExistsWithEmptyEmailTest() {
        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .id(1L)
                .name("user2")
                .build();

        User user = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("user2")
                .email("user1@mail.ru")
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponse result = userService.updateUser(user.getId(), updateUserRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);
        verify(userRepository).findById(user.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void updateUserWhenUserNotFoundTest() {
       UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .id(1L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        User user = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(user.getId(), updateUserRequest));

        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());

        assertTrue(exception.getMessage().contains(String.valueOf(user.getId())));
        verify(userRepository, Mockito.times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void deleteUserByIdTest() {
        long userId = 1L;
        userService.deleteUserById(userId);

        verify(userRepository).deleteById(anyLong());
    }

    @Test
    public void testHashCodeForUserClass() {
        User user = new User();
        int expectedHashCode = User.class.hashCode();

        assertEquals(expectedHashCode, user.hashCode());
    }
}