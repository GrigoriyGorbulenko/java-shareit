package ru.practicum.shareit.request.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(HEADER) Long userId,
                                                    @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByRequesterId(@RequestHeader(HEADER) Long userId) {
        return itemRequestClient.getAllItemRequestsByRequesterId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object>  getAllItemRequestsExceptUserId(
            @RequestHeader(HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.getAllItemRequestsExceptUserId(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object>  getItemRequestById(@PathVariable("requestId") Long requestId,
                                                 @RequestHeader(HEADER) Long userId) {
        return itemRequestClient.getItemRequestById(requestId, userId);
    }
}
