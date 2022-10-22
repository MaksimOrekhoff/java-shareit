package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@Transactional
class ItemRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Test
    void search() {
        userRepository.save(new User(1L, "user10", "user10@mail.com"));
        Item item = itemRepository.save(new Item(1L, "item1", "отвертка", true, 1L, null));

        List<Item> result = itemRepository.search("Отвер");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getAvailable(), result.get(0).getAvailable());
    }

}