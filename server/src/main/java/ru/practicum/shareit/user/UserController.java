package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        log.info("Получен запрос POST на создание пользователя {}", request);
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        log.info("Поступил запрос GET на получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable("userId") long userId) {
        log.info("Поступил запрос GET на получение данных о пользователе с id = {}", userId);
        return userService.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUser(@PathVariable("userId") long userId,
                                   @RequestBody UpdateUserRequest request) {
        log.info("Получен запрос PATCH на обновление пользователя {}", request);
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") long userId) {
        log.info("Получен запрос DELETE на удаление пользователя c id = {}", userId);
        userService.deleteUserById(userId);
    }
}