package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDtoBooking getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен Get-запрос на получение вещи c id: {}", itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam final String text,
                                           @RequestParam(defaultValue = "0") final int from,
                                           @RequestParam(defaultValue = "100") final int size) {
        log.info("Получен запрос на поиск вещей содержащих {} ", text);

        return itemService.searchItems(text, from, size);
    }

    @GetMapping()
    public Collection<ItemDtoBooking> getAllItemsUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен Get-запрос на получение вещей пользователя c id: {}", userId);
        return itemService.getAllItemsUser(userId, from, size);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable long itemId,
                             @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление комментария к вещи {} от пользователя {}", itemId, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
