package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;


    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers()
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = checkUser(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        List<User> users = new ArrayList<>(userStorage.getAllUsers());
        checkUserEmail(users, userDto);
        return userMapper.toUserDto(userStorage.createUser(userMapper.toUser(userDto)));
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = checkUser(userId);
        List<User> users = new ArrayList<>(userStorage.getAllUsers());
        users.remove(user);
        checkUserEmail(users, userDto);
        return userMapper.toUserDto(userStorage.updateUser(userId, userMapper.toUser(userDto)));
    }

    private void checkUserEmail(List<User> users, UserDto userDto) {
        users
                .stream()
                .filter(user -> user.getEmail().equals(userDto.getEmail()))
                .forEach(user -> {
                    throw new InternalServerException("Пользователь с таким email уже существует");
                });
    }

    private User checkUser(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}
