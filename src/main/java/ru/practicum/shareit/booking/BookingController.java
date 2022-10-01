package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.dto.BookingDtoUpdate;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.addNewBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoUpdate changeBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoGet getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoGet> getAllBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBooking(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoGet> getBookingOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findBookingOwner(userId, state);
    }
}
