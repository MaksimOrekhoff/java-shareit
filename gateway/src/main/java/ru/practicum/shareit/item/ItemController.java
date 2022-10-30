package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


/**
 * TODO Sprint add-controllers.
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен Get-запрос на получение вещи c id: {}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam final String text,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                              @RequestParam(defaultValue = "100") @Positive final int size) {
        log.info("Получен запрос на поиск вещей содержащих {} ", text);
        return itemClient.search(text, from, size);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllItemsUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен Get-запрос на получение вещей пользователя c id: {}", userId);
        return itemClient.getItems(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Validated({Create.class})@RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable long itemId,
                                         @RequestBody ItemDto itemDto) {
        return itemClient.patchItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable long itemId,
                                         @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление комментария к вещи {} от пользователя {}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
