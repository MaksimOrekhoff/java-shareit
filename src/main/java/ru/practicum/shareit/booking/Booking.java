package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
public class Booking {
    private final long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final long item;
    private final long booker;
    private Enum status;

}
