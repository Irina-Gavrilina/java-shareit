package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.*;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        User user = userRepository.save(UserMapper.toUser(request));
        return UserMapper.toUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> listOfAllUsers = userRepository.findAll();
        return UserMapper.toListOfUsersResponse(listOfAllUsers);
    }

    @Override
    public UserResponse getUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(
                "Пользователя с id = %d нет в базе", userId)));
        return UserMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format(
                "Пользователя с id = %d нет в базе", userId)));
        if (request.hasName()) {
            user.setName(request.getName());
        }
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        return UserMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }
}