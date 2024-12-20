package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingRequestDto bookingRequestDto) {
        User user = checkUser(userId);
        Item item = checkItem(bookingRequestDto.getItemId());
        validateItem(bookingRequestDto, item, user);
        Booking booking = BookingMapper.toBooking(bookingRequestDto, item, user);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto updateBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking = checkBooking(bookingId);
        Item item = booking.getItem();
        userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Пользователь с id = " + userId + " не найден"));
        if (!userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("У пользователя нет вещи с id " + item.getId());
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Некорректный статус брони");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getBookingByUserId(Long bookingId, Long userId) {
        checkUser(userId);
        checkBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).filter(booking1 ->
                booking1.getBooker().getId() == userId
                        || booking1.getItem().getOwner().getId() == userId).orElseThrow(() ->
                new NotFoundException("Пользователь не является владельцем вещи"));
        ;
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByBookerId(Long bookerId, String state) {
        checkUser(bookerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (State.valueOf(state)) {
            case ALL -> bookingRepository.findByBooker_Id(bookerId);
            case CURRENT ->
                    bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(bookerId, now, now);
            case PAST -> bookingRepository.findByBooker_IdAndEndIsBeforeOrderByEndDesc(bookerId, now);
            case FUTURE -> bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(bookerId, now);
            case WAITING -> bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(bookerId, Status.WAITING);
            case REJECTED -> bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(bookerId, Status.REJECTED);
        };

        return bookings.stream().map(BookingMapper::toBookingResponseDto)
                .collect(toList());
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByOwnerId(Long ownerId, String state) {
        checkUser(ownerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (State.valueOf(state)) {
            case ALL -> bookingRepository.findByItem_Owner_IdOrderByStartDesc(ownerId);
            case CURRENT ->
                    bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, now, now);
            case PAST -> bookingRepository.findByItem_Owner_IdAndEndIsBeforeOrderByEndDesc(ownerId, now);
            case FUTURE -> bookingRepository.findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(ownerId, now);
            case WAITING -> bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, Status.WAITING);
            case REJECTED -> bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, Status.REJECTED);
        };

        return bookings.stream().map(BookingMapper::toBookingResponseDto)
                .collect(toList());
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    private Booking checkBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с id = " + bookingId + " не найдена"));
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));
    }

    private void validateItem(BookingRequestDto bookingDtoRequest, Item item, User booker) {
        if (item.getOwner().getId().equals(booker.getId())) {
            throw new ValidationException("Забронировать свою вещь невозможно");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Бронь недоступна");
        }
        if (bookingDtoRequest.getStart().isAfter(bookingDtoRequest.getEnd())) {
            throw new ValidationException("Время брони указано некорректно");
        }
        if (bookingDtoRequest.getStart().equals(bookingDtoRequest.getEnd())) {
            throw new ValidationException("Время брони указано некорректно");
        }
    }
}