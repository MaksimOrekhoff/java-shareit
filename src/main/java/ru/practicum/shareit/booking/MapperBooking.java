package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.dto.BookingDtoUpdate;


@Component
public class MapperBooking {
    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getItemId(),
                booking.getStart(),
                booking.getEnd(),
                StatusItem.WAITING);
    }

    public BookingDtoUpdate toBookingDtoApp(Booking booking) {
        return new BookingDtoUpdate(booking.getId(),
                booking.getStatus(),
                null,
                null);
    }

    public Booking toBooking(BookingDto bookingDto) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItemId(),
                null,
                null);
    }

    public BookingDtoGet bookingDtoGet(Booking booking) {
        return new BookingDtoGet(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                null,
                null);
    }
}
