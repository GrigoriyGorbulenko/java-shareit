package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> itemStorage = new HashMap<>();
    private long idGenerator = 1;

    @Override
    public Item createItem(Item item) {
        item.setId(idGenerator++);
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.of(itemStorage.get(itemId));
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        return itemStorage.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .toList();
    }

    @Override
    public Collection<Item> getSearch(String text) {
        return itemStorage.values();
    }


    @Override
    public Item updateItem(Long itemId, ItemDto itemDto) {
        Item item = itemStorage.get(itemId);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}
