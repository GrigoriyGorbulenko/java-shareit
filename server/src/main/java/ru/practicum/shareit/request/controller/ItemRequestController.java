package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestResponseDto createItemRequest(@RequestHeader(HEADER) Long userId,
                                                 @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getAllItemRequestsByRequesterId(@RequestHeader(HEADER) Long userId) {
        return itemRequestService.getAllItemRequestsByRequesterId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllItemRequestsExceptUserId(
            @RequestHeader(HEADER) Long userId) {
        return itemRequestService.getAllItemRequestsExceptUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getItemRequestById(@PathVariable("requestId") Long requestId,
                                                 @RequestHeader(HEADER) Long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
