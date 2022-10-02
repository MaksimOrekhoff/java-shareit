package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.dto.BookingDtoUpdate;

import java.util.List;

public interface BookingService {


    BookingDto addNewBooking(Long userId, BookingDto bookingDto);

    BookingDtoUpdate update(Long userId, Long bookingId, Boolean approved);

    BookingDtoGet findBooking(Long userId, Long bookingId);

    List<BookingDtoGet> findAllBooking(Long userId, String state);

    List<BookingDtoGet> findBookingOwner(Long userId, String state);
}
