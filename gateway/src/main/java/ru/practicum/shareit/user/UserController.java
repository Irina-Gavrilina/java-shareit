package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@RequestBody @Valid CreateUserRequest request) {
        log.info("Получен запрос POST на создание пользователя {}", request);
        return userClient.createUser(request);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Поступил запрос GET на получение списка всех пользователей");
        return  userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable("userId") long userId) {
        log.info("Поступил запрос GET на получение данных о пользователе с id = {}", userId);
        return userClient.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") long userId,
                                   @RequestBody @Valid UpdateUserRequest request) {
        log.info("Получен запрос PATCH на обновление пользователя {}", request);
        return userClient.updateUser(userId,request);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable("userId") long userId) {
        log.info("Получен запрос DELETE на удаление пользователя c id = {}", userId);
        return userClient.deleteUserById(userId);
    }
}