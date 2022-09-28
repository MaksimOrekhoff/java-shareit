package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, long itemId, ItemDto itemDto);

    ItemDto getItem(Long itemId);

    Collection<ItemDto> getAllItemsUser(Long userId);

    Collection<ItemDto> searchItems(String text);
}
