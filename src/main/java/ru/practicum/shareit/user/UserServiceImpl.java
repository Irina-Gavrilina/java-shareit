package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toListOfUsersDto(userRepository.getAllUsers());
    }

    @Override
    public UserDto getUserById(long userId) {
        Optional<User> optUser = userRepository.getUserById(userId);
        if (optUser.isPresent()) {
            return UserMapper.toUserDto(optUser.get());
        }
        log.error("Пользователь с id = {} не найден", userId);
        throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", userId));
    }

    @Override
    public UserDto updateUser(long userId, UserDto newUserDto) {
        User newUser = UserMapper.toUser(newUserDto);
        return UserMapper.toUserDto(userRepository.updateUser(userId, newUser));
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteUserById(userId);
    }
}