package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(Item item);

    Optional<Item> getItemById(Long itemId);

    List<Item> getAllItemsByUserId(Long userId);

    Collection<Item> getSearch(String text);
    Item updateItem(Long itemId, ItemDto itemDto);
}
