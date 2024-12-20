package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader(HEADER) Long userId,
            @Valid @RequestBody ItemRequestsDto itemRequestsDto) {
        return itemClient.createItem(itemRequestsDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(HEADER) Long ownerId,
            @PathVariable("itemId") Long itemId) {
        return itemClient.getItemById(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(
            @RequestHeader(HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getAllItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearch(
            @RequestHeader(HEADER) Long userId,
            @RequestParam String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getSearch(userId, text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(HEADER) Long userId,
            @PathVariable("itemId") Long itemId,
            @RequestBody ItemRequestsDto itemRequestsDto) {
        return itemClient.updateItem(itemId, userId, itemRequestsDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader(HEADER) Long userId,
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {

        return itemClient.createComment(itemId, userId, commentRequestDto);
    }
}
