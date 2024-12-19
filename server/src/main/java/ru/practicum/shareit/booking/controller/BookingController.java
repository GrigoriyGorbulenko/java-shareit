package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponseDto createBooking(
            @RequestHeader(HEADER) Long userId,
            @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.createBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(
            @PathVariable("bookingId") Long bookingId,
            @RequestHeader(HEADER) Long userId,
            @RequestParam(name = "approved") @NotNull Boolean approved) {
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingByUserId(
            @PathVariable("bookingId") Long bookingId,
            @RequestHeader(HEADER) Long userId) {
        return bookingService.getBookingByUserId(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingsByBookerId(
            @RequestHeader(HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsByOwnerId(
            @RequestHeader(HEADER) Long ownerId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByOwnerId(ownerId, state);
    }
}
