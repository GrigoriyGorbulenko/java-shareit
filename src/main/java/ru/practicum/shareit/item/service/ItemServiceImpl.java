package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;



@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {

        User user = checkUser(userId);
        itemDto.setOwnerId(user.getId());
        return itemMapper.toItemDto(itemStorage.createItem(itemMapper.toItem(itemDto)));

    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemMapper.toItemDto(checkItem(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        checkUser(userId);
        return itemStorage.getAllItemsByUserId(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getSearch(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        List<Item> itemSearch = itemStorage.getSearch(text)
                .stream()
                .filter(item -> item.getName() != null && item.getDescription() != null)
                .filter(item ->
                        (item.getName().toUpperCase().contains(text.toUpperCase()) || item.getDescription().toUpperCase()
                                .contains(text.toUpperCase())) && item.getAvailable()).toList();
        return itemSearch
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Long ownerId = checkItem(itemId).getOwner().getId();
        if (!userId.equals(ownerId)) {
            throw new NotFoundException("У пользователя нет вещи с id " + itemId);
        }
        return itemMapper.toItemDto(itemStorage.updateItem(itemId, itemDto));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private Item checkItem(Long itemId) {
        return itemStorage.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Веещь с id = " + itemId + " не найдена"));
    }
}
