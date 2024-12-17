package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto updateBooking(Long bookingId, Long userId, Boolean approved);

    BookingResponseDto getBookingByUserId(Long bookingId, Long userId);

    List<BookingResponseDto> getAllBookingsByBookerId(Long bookerId, String state);

    List<BookingResponseDto> getAllBookingsByOwnerId(Long ownerId, String state);
}
