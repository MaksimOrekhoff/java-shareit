package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.dto.BookingDtoUpdate;
import ru.practicum.shareit.exceptions.InvalidStatusException;
import ru.practicum.shareit.exceptions.NotAvailableBooking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoUp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoUp;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final MapperBooking mapperBooking;

    @Override
    public BookingDto addNewBooking(Long userId, BookingDto bookingDto) {
        validation(userId, bookingDto);
        Booking booking = mapperBooking.toBooking(bookingDto, userId);
        Booking newBooking = bookingRepository.save(booking);
        return mapperBooking.toBookingDto(newBooking);
    }

    @Override
    public BookingDtoUpdate update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого бронирования не существует."));
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Такая вещь не существует."));
        validUpdate(item, booking, userId);
        StatusItem statusItem = approved ? StatusItem.APPROVED : StatusItem.REJECTED;
        booking.setStatus(statusItem);
        Booking newBooking = bookingRepository.save(booking);
        return mapperBooking.toBookingDtoApp(newBooking,
                new ItemDtoUp(item.getId(), item.getName()),
                new UserDtoUp(newBooking.getBooker()));
    }

    @Override
    public BookingDtoGet findBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Такого бронирования не существует."));
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Такой вещи не существует."));
        if (!(Objects.equals(userId, booking.getBooker()) ||
                Objects.equals(item.getUserId(), userId))) {
            throw new NotFoundException("Нет прав.");
        }
        return mapperBooking.bookingDtoGet(booking,
                new UserDtoUp(booking.getBooker()),
                new ItemDtoUp(item.getId(), item.getName()));
    }


    @Override
    public List<BookingDtoGet> findAllBooking(Long userId, String state) {
        validGet(userId);
        return sendBookingDto(stateAll(userId), state);
    }

    private List<BookingDtoGet> sendBookingDto(List<BookingDtoGet> bookingDtoGet, String state) {
        switch (state) {
            case "ALL":
                return bookingDtoGet;
            case "CURRENT":
                return bookingDtoGet.stream()
                        .filter(bookingDtoGt -> bookingDtoGt.getEnd().isAfter(LocalDateTime.now())
                                && bookingDtoGt.getStart().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return bookingDtoGet.stream()
                        .filter(bookingDtoGt -> bookingDtoGt.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingDtoGet.stream()
                        .filter(bookingDtoGt -> bookingDtoGt.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingDtoGet.stream()
                        .filter(bookingDtoGt -> bookingDtoGt.getStatus().equals(StatusItem.WAITING))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingDtoGet.stream()
                        .filter(bookingDtoGt -> bookingDtoGt.getStatus().equals(StatusItem.REJECTED))
                        .collect(Collectors.toList());
            default:
                throw new InvalidStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDtoGet> findBookingOwner(Long userId, String state) {
        List<Long> id = itemRepository.findAll().stream()
                .filter(item -> item.getUserId() == userId)
                .map(Item::getId).collect(Collectors.toList());
        if (id.isEmpty()) {
            throw new NotFoundException("У пользователя нет вещей.");
        }
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(booking -> id.contains(booking.getItemId()))
                .sorted(new BookingComparator())
                .collect(Collectors.toList());
        List<BookingDtoGet> bookingDtoGets = getBookingDtoGets(bookings);
        return sendBookingDto(bookingDtoGets, state);
    }

    private List<BookingDtoGet> getBookingDtoGets(List<Booking> bookings) {
        List<BookingDtoGet> bookingDtoGets = new ArrayList<>();
        for (Booking booking : bookings) {
            Item item = itemRepository.findById(booking.getItemId()).get();
            BookingDtoGet bookingDtoGet = mapperBooking.bookingDtoGet(booking,
                    new UserDtoUp(booking.getBooker()),
                    new ItemDtoUp(item.getId(), item.getName()));
            bookingDtoGets.add(bookingDtoGet);
        }
        return bookingDtoGets;
    }


    private List<BookingDtoGet> stateAll(Long userId) {
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(booking -> Objects.equals(booking.getBooker(), userId))
                .sorted(new BookingComparator())
                .collect(Collectors.toList());
        return getBookingDtoGets(bookings);
    }

    private void validation(Long userId, BookingDto bookingDto) {
        validGet(userId);
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(booking -> booking.getStatus().equals(StatusItem.APPROVED))
                .filter(booking -> Objects.equals(booking.getItemId(), bookingDto.getItemId()))
                .filter(booking -> booking.getStart().isAfter(bookingDto.getStart())
                        && booking.getEnd().isBefore(bookingDto.getStart())
                        || booking.getStart().isAfter(bookingDto.getEnd())
                        && booking.getEnd().isBefore(bookingDto.getEnd()))
                .collect(Collectors.toList());
        if (!bookings.isEmpty()) {
            throw new NotAvailableBooking("Данный период не доступен для бронирования.");
        }
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Такая вещь не существует."));
        if (!item.getAvailable()) {
            throw new NotAvailableBooking("Не доступно для бронирования.");
        }
        if (item.getUserId() == userId) {
            throw new NotFoundException("Для Вас не доступна.");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new NotAvailableBooking("Окончание бронирование раньше начала.");
        }
    }

    private void validUpdate(Item item, Booking booking, Long userId) {
        if (item.getUserId() != userId) {
            throw new NotFoundException("Нет прав.");
        }
        if (booking.getStatus().equals(StatusItem.APPROVED)) {
            throw new NotAvailableBooking("Статус уже был подтвержден.");
        }
    }

    private void validGet(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такой пользователь не существует."));
    }

    public static class BookingComparator implements Comparator<Booking> {
        public int compare(Booking a, Booking b) {
            if (a.getStart().equals(b.getStart())) {
                return 0;
            } else if (a.getStart().isBefore(b.getStart())) {
                return 1;
            }
            return -1;
        }
    }
}
