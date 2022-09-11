package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class ItemDB {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    public Item create(ItemDto itemDto, long idUser) {
        id++;
        Item item = toItem(itemDto, id, idUser);
        items.put(id, item);
        return item;
    }


    public Item getItem(Long itemId) {
        return items.get(itemId);
    }

    public Collection<Item> getAllItemsUser(Long userId) {
        return items.values().stream()
                .filter(item -> item.getUserId() == userId)
                .collect(Collectors.toList());
    }

    public Item update(Long userId, long itemId, ItemDto itemDto) {
        Item item = items.get(itemId);
        validation(item, userId);
        Item newItem = new Item(itemId,
                itemDto.getName() == null ? item.getName() : itemDto.getName(),
                itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription(),
                itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable(),
                userId
        );
        items.put(itemId, newItem);
        return newItem;
    }

    public List<Item> searchItems(String text) {
        Stream<Item> itemStreamName = items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()));
        Stream<Item> itemStreamDescription = items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()));
        return Stream.concat(itemStreamName, itemStreamDescription).distinct().collect(Collectors.toList());
    }

    private Item toItem(ItemDto itemDto, long id, long idUser) {
        return new Item(id,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                idUser);
    }

    private void validation(Item item, long userId) {
        if (item == null) {
            throw new NotFoundException("Такая вещь не существует.");
        }
        if (item.getUserId() != userId) {
            throw new NotFoundException("Этот пользователь не имеет прав на изменение.");
        }
    }
}
