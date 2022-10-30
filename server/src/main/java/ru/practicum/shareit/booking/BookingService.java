package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;

import java.util.List;

public interface BookingService {

    BookingDtoGet addNewBooking(Long userId, BookingDto bookingDto);

    BookingDtoGet update(Long userId, Long bookingId, Boolean approved);

    BookingDtoGet findBooking(Long userId, Long bookingId);


    List<BookingDtoGet> findAllBooking(Long userId, String state, Integer from, Integer size);

    List<BookingDtoGet> findBookingOwner(Long userId, String state, Integer from, Integer size);
}
