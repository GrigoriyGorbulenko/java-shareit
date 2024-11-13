package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage{

    private final Map<Long, User> userStorage = new HashMap<>();
    private long idGenerator = 1;

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.values();
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.of(userStorage.get(userId));
    }

    @Override
    public User createUser(User newUser) {
        newUser.setId(idGenerator++);
        userStorage.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.remove(userId);
    }

    @Override
    public User updateUser(Long userId, User newUser) {
        User oldUser = userStorage.get(userId);
        oldUser.setName(newUser.getName());
        oldUser.setEmail(newUser.getEmail());
        return oldUser;
    }
}
