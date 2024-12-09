package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;



@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {

        User user = checkUser(userId);
        Item item = itemMapper.toItem(itemDto);
        itemDto.setOwnerId(userId);
        return itemMapper.toItemDto(itemRepository.save(itemMapper.toItem(itemDto)));

    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemMapper.toItemDto(checkItem(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        checkUser(userId);
        return itemRepository.findAllByOwnerIdOrderById(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getSearch(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        return itemRepository.search(text)
                .stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = checkItem(itemId);

        if (!userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("У пользователя нет вещи с id " + itemId);
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(item);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Веещь с id = " + itemId + " не найдена"));
    }
}
