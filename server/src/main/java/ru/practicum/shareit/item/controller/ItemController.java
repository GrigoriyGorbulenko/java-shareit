package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(
            @RequestHeader(HEADER) Long userId,
            @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader(HEADER) Long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearch(@RequestParam String text) {
        return itemService.getSearch(text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable("itemId") Long itemId,
            @RequestHeader(HEADER) Long userId,
            @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(
            @PathVariable("itemId") Long itemId,
            @RequestHeader(HEADER) Long userId,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {

        return itemService.createComment(itemId, userId, commentRequestDto);
    }
}
