package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User createUser(User user);

    List<User> getAllUsers();

    Optional<User> getUserById(long userId);

    User updateUser(long userId, User newUser);

    void deleteUserById(long userId);
}