package ru.practicum.shareit.user.service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;



public interface UserService {
    Collection<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    UserDto createUser(UserDto userDto);

    void deleteUser(Long userId);

    UserDto updateUser(Long userId, UserDto userDto);
}
