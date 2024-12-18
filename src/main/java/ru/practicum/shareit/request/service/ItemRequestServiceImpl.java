package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemRequestResponseDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = checkUser(userId);
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, user));
        List<Item> items = itemRepository.findByRequest(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestResponseDto> getAllItemRequestsByRequesterId(Long userId) {
        User user = checkUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterOrderByCreatedDesc(user);
        return itemRequests.stream()
                .map(tempItemRequest -> {
                    List<Item> items = itemRepository.findByRequest(tempItemRequest);
                    return ItemRequestMapper.toItemRequestDto(tempItemRequest, items);
                })
                .toList();
    }

    @Override
    public List<ItemRequestResponseDto> getAllItemRequestsExceptUserId(Long userId) {
        User user = checkUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterNotOrderByCreatedDesc(user);
        return itemRequests.stream()
                .map(tempItemRequest -> {
                    List<Item> items = itemRepository.findByRequest(tempItemRequest);
                    return ItemRequestMapper.toItemRequestDto(tempItemRequest, items);
                })
                .toList();
    }

    @Override
    public ItemRequestResponseDto getItemRequestById(Long requestId, Long userId) {
        checkUser(userId);
        ItemRequest itemRequest = checkItemRequest(requestId);
        List<Item> items = itemRepository.findByRequest(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private ItemRequest checkItemRequest(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос c id " + requestId + " не найден"));
    }
}
