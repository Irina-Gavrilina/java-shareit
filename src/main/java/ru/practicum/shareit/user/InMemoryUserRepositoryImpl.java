package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class InMemoryUserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        if (isEmailAlreadyExists(user.getEmail())) {
            log.error("Email '{}' уже используется", user.getEmail());
            throw new DuplicateDataException("Этот email уже используется");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return users.values()
                .stream()
                .toList();
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User updateUser(long userId, User newUser) {
        if (!users.containsKey(userId)) {
            log.error("Пользователь с id = {} не найден", newUser.getId());
            throw new NotFoundException(String.format("Пользователя с id = %d нет в базе", newUser.getId()));
        }
        if (isEmailAlreadyExists(newUser.getEmail())) {
            log.error("Email '{}' уже используется", newUser.getEmail());
            throw new DuplicateDataException("Этот email уже используется");
        }

        User oldUser = users.get(userId);

        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        users.put(userId, oldUser);
        return oldUser;
    }

    @Override
    public void deleteUserById(long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("Не найден пользователь с id = %d для удаления", userId));
        }
        users.remove(userId);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(userId -> userId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean isEmailAlreadyExists(String currentEmail) {
        return users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(currentEmail));
    }
}