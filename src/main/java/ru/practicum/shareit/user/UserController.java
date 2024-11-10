package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос POST на создание пользователя {}", userDto);
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Поступил запрос GET на получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") long userId) {
        log.info("Поступил запрос GET на получение данных о пользователе с id = {}", userId);
        return userService.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") long userId,
                              @RequestBody UserDto newUserDto) {
        log.info("Получен запрос PATCH на обновление пользователя {}", newUserDto);
        return userService.updateUser(userId, newUserDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") long userId){
        log.info("Получен запрос DELETE на удаление пользователя c id = {}", userId);
        userService.deleteUserById(userId);
    }
}