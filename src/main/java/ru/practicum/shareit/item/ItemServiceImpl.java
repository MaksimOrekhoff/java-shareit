package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.exceptions.NotAvailableBooking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemDB;
    private final UserRepository userDB;
    private final BookingRepository bookingRepository;
    private final MapperItems mapperItems;
    private final CommentDB commentDB;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        List<Long> ids = userDB.findAll().stream().map(User::getId).collect(Collectors.toList());
        if (ids.contains(userId)) {
            Item newItem = mapperItems.toItem(
                    itemDto,
                    userId,
                    null
            );
            Item item = itemDB.save(newItem);
            log.debug("Добавлена вещь: {}", item);
            return mapperItems.toItemDto(item);
        }
        throw new NotFoundException("Такой пользователь не сущетсвует.");
    }

    @Override
    public ItemDto update(Long userId, long itemId, ItemDto itemDto) {
        List<Long> ids = userDB.findAll().stream()
                .map(User::getId)
                .collect(Collectors.toList());
        if (!ids.contains(userId)) {
            throw new NotFoundException("Такой пользователь не сущетсвует.");
        }

        List<Long> idItems = itemDB.findAll().stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        if (!idItems.contains(itemId)) {
            throw new NotFoundException("Такая вещь не сущетсвует.");
        }

        Item checkOwner = itemDB.findById(itemId).get();

        if (checkOwner.getUserId() != userId) {
            log.debug("У вас нет прав для изменения вещи: {}", checkOwner);
            throw new NotFoundException("Это не ваша вещь.");
        }

        Item newItem = new Item(itemId,
                itemDto.getName() == null ? checkOwner.getName() : itemDto.getName(),
                itemDto.getDescription() == null ? checkOwner.getDescription() : itemDto.getDescription(),
                itemDto.getAvailable() == null ? checkOwner.getAvailable() : itemDto.getAvailable(),
                userId,
                null
        );
        Item item = itemDB.save(newItem);
        return mapperItems.toItemDto(item);
    }

    @Override
    public ItemDtoBooking getItem(Long itemId, Long userId) {
        Optional<Item> item = itemDB.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Такая вещь не сущетсвует.");
        }
        ItemDtoBooking itemDtoBooking = mapperItems.toItemDtoBooking(item.get());

        itemDtoBooking.setComments(toCommentDtos(itemId));

        if (userId != item.get().getUserId()) {
            return itemDtoBooking;
        }
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(booking -> booking.getItemId().equals(itemId))
                .sorted(new BookingServiceImpl.BookingComparator())
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            return itemDtoBooking;
        }
        if (bookings.size() == 1) {
            Booking booking = bookings.get(0);
            if (booking.getEnd().isAfter(LocalDateTime.now())) {
                itemDtoBooking.setLastBooking(new BookingDtoItem(bookings.get(0).getId(),
                        bookings.get(0).getBooker()));
                return itemDtoBooking;
            } else {
                itemDtoBooking.setNextBooking(new BookingDtoItem(bookings.get(0).getId(),
                        bookings.get(0).getBooker()));
            }

        }
        List<Booking> next = bookings.stream().filter(booking -> booking.getStart().isBefore(LocalDateTime.now())).collect(Collectors.toList());
        itemDtoBooking.setLastBooking(new BookingDtoItem(next.get(0).getId(), next.get(0).getBooker()));
        List<Booking> last = bookings.stream().filter(booking -> booking.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
        itemDtoBooking.setNextBooking(new BookingDtoItem(last.get(0).getId(), last.get(0).getBooker()));
        return itemDtoBooking;
    }

    private List<CommentDto> toCommentDtos(Long itemId) {
        List<Comment> comments = commentDB.findAll().stream()
                .filter(comment -> comment.getItemId().equals(itemId))
                .collect(Collectors.toList());
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment c : comments) {
            Optional<User> user = userDB.findById(c.getAuthorId());
            CommentDto commentDto = new CommentDto(c.getId(), c.getText(), user.get().getName(), c.getCreated());
            commentDtos.add(commentDto);
        }
        return commentDtos;
    }

    @Override
    public Collection<ItemDtoBooking> getAllItemsUser(Long userId) {
        Collection<Item> items = itemDB.findAll().stream()
                .filter(item -> item.getUserId() == userId)
                .collect(Collectors.toList());
        Collection<ItemDtoBooking> itemDtoBookings = new ArrayList<>();
        for (Item i : items) {
            ItemDtoBooking itemDtoBooking = getItem(i.getId(), userId);
            itemDtoBookings.add(itemDtoBooking);
        }
        return itemDtoBookings;
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text.length() == 0) {
            return new ArrayList<>();
        }
        Collection<Item> items = itemDB.search(text).stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
        return collectionDto(items);

    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        validComment(userId, itemId);
        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(booking -> Objects.equals(booking.getItemId(), itemId)
                        && Objects.equals(booking.getBooker(), userId)
                        && booking.getEnd().isBefore(LocalDateTime.now()))
                .sorted(new BookingServiceImpl.BookingComparator())
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new NotAvailableBooking("Бронирование не окончено.");
        }
        Optional<User> user = userDB.findById(userId);
        Comment comment = new Comment(null, commentDto.getText(), itemId, userId, LocalDateTime.now());
        commentDto.setAuthorName(user.get().getName());
        commentDto.setCreated(LocalDateTime.now());
        Comment comment1 = commentDB.save(comment);
        commentDto.setId(comment1.getId());
        commentDto.setAuthorName(user.get().getName());
        return commentDto;
    }

    private Collection<ItemDto> collectionDto(Collection<Item> items) {
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(mapperItems.toItemDto(item));
        }
        return itemsDto;
    }

    private void validComment(Long userId, Long itemId) {
        Optional<Item> item = itemDB.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Такая вещь не сущетсвует.");
        }
        Optional<User> user = userDB.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Такая пользователь не сущетсвует.");
        }

    }
}
