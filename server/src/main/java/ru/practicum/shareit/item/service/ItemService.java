package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {
        ItemDto createItem(Long userId, ItemDto itemDto);

        ItemDto getItemById(Long itemId);

        List<ItemDto> getAllItemsByUserId(Long userId);

        List<ItemDto> getSearch(String text);

        ItemDto updateItem(Long itemId, Long userid, ItemDto itemDto);

        CommentResponseDto createComment(Long itemId, Long userId, CommentRequestDto commentRequestDto);

        List<CommentResponseDto> getCommentsByItemId(Long itemId);
}
