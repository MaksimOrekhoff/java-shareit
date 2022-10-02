package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.dto.BookingDtoUpdate;
import ru.practicum.shareit.item.dto.ItemDtoUp;
import ru.practicum.shareit.user.dto.UserDtoUp;


@Component
public class MapperBooking {
    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getItemId(),
                booking.getStart(),
                booking.getEnd(),
                StatusItem.WAITING);
    }

    public BookingDtoUpdate toBookingDtoApp(Booking booking, ItemDtoUp itemDtoUp, UserDtoUp userDtoUp) {
        return new BookingDtoUpdate(booking.getId(),
                booking.getStatus(),
                userDtoUp,
                itemDtoUp);
    }

    public Booking toBooking(BookingDto bookingDto, Long userId) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItemId(),
                userId,
                StatusItem.WAITING);
    }

    public BookingDtoGet bookingDtoGet(Booking booking, UserDtoUp userDtoUp, ItemDtoUp itemDtoUp) {
        return new BookingDtoGet(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                userDtoUp,
                itemDtoUp);
    }
}
