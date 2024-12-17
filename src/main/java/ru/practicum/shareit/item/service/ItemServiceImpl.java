package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = checkUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        Item saveItem = itemRepository.save(item);
        return ItemMapper.toItemDto(saveItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = checkItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(commentRepository.findAllByItem_Id(itemId).stream()
                .map(CommentMapper::toCommentResponseDto)
                .toList());
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        checkUser(userId);
        return itemRepository.findAllByOwnerIdOrderById(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getSearch(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
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
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    public CommentResponseDto createComment(Long itemId, Long userId, CommentRequestDto commentRequestDto) {
        Item item = checkItem(itemId);
        User user = checkUser(userId);
        List<Booking> booking = bookingRepository.findByBooker_IdAndItem_IdAndEndIsBeforeAndStatus(userId,
                itemId, LocalDateTime.now(), Status.APPROVED);
        if (booking.isEmpty()) {
            throw new ValidationException("Параметры указаны некорректно");
        }
        Comment comment = CommentMapper.toComment(commentRequestDto, item, user);
        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentResponseDto> getCommentsByItemId(Long itemId) {
        return commentRepository
                .findAllByItem_Id(itemId)
                .stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(toList());
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
