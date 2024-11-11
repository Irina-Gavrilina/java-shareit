package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    UserDto updateUser(long userId, UserDto newUserDto);

    void deleteUserById(long userId);
}