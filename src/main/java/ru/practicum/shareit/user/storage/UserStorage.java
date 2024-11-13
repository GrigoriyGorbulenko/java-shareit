package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAllUsers();

    Optional<User> getUserById(Long userId);

    User createUser(User newUser);

    void deleteUser(Long userId);

    User updateUser(Long userId, User newUser);
}
