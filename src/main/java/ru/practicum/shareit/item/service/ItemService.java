package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {
        ItemDto createItem(Long userId, ItemDto itemDto);

        ItemDto getItemById(Long itemId);

        List<ItemDto> getAllItemsByUserId(Long userId);

        List<ItemDto> getSearch(String text);

        ItemDto updateItem(Long userid, Long itemId, ItemDto itemDto);
}
