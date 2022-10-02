package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.StatusItem;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private Long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
    private StatusItem status;
}
