package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.util.Collection;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, long itemId, ItemDto itemDto);

    ItemDtoBooking getItem(Long itemId, Long userId);

    Collection<ItemDtoBooking> getAllItemsUser(Long userId, Integer from, Integer size);

    Collection<ItemDto> searchItems(String text, Integer from, Integer size);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
