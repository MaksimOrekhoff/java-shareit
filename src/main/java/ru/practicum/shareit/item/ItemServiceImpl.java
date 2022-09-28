package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDB;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDB itemDB;
    private final UserDB userDB;
    private final MapperItems mapperItems;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        if (userDB.getUsers().containsKey(userId)) {
            Item item = itemDB.create(itemDto, userId);
            log.debug("Добавлена вещь: {}", item);
            return mapperItems.toItemDto(item);
        }
        throw new NotFoundException("Такой пользователь не сущетсвует.");
    }

    @Override
    public ItemDto update(Long userId, long itemId, ItemDto itemDto) {
        Item item = itemDB.update(userId, itemId, itemDto);
        return mapperItems.toItemDto(item);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemDB.getItem(itemId);
        if (item == null) {
            throw new NotFoundException("Такая вещь не сущетсвует.");
        }
        return mapperItems.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItemsUser(Long userId) {
        Collection<Item> items = itemDB.getAllItemsUser(userId);
        return collectionDto(items);

    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text.length() == 0) {
            return new ArrayList<>();
        }
        Collection<Item> items = itemDB.searchItems(text);
        return collectionDto(items);
    }

    private Collection<ItemDto> collectionDto(Collection<Item> items) {
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(mapperItems.toItemDto(item));
        }
        return itemsDto;
    }
}
