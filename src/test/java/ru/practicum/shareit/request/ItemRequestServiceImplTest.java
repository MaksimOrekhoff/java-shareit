package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUserDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@Transactional
class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final MapperItemRequest mapperItemRequest;
    User user;
    User user1;
    ItemRequest itemRequest;
    Item item;

    @BeforeEach
    void start() {
        user = new User(1L, "user", "user@email.com");
        itemRequest = new ItemRequest(1L, "newRequest", 1L, LocalDateTime.now());
        user1 = new User(2L, "user", "user1@email.com");
        item = new Item(1L, "item", "newItem", true, 2L, 1L);
    }

    @AfterEach
    void clean() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void create() {
        ItemRequestDto itemRequestDto = mapperItemRequest.toItemRequestDto(itemRequest);
        User requester = userRepository.save(user);
        ItemRequestDto newItemRequest = itemRequestService.create(itemRequestDto, requester.getId());

        assertNotNull(newItemRequest);
        assertEquals(newItemRequest.getId(), itemRequest.getId());
        assertEquals(newItemRequest.getDescription(), itemRequest.getDescription());
    }

    @Test
    public void createException() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            ItemRequestDto itemRequestDto = mapperItemRequest.toItemRequestDto(itemRequest);
            itemRequestService.create(itemRequestDto, 100L);
        });

        Assertions.assertEquals("Такой пользователь не существует.", thrown.getMessage());
    }

    @Test
    void findById() {
        ItemRequestDto itemRequestDto = mapperItemRequest.toItemRequestDto(itemRequest);
        User requester = userRepository.save(user);
        ItemRequestDto newItemRequest = itemRequestService.create(itemRequestDto, requester.getId());

        ItemRequestUserDto itemRequestUserDto = itemRequestService.findById(newItemRequest.getId(), requester.getId());

        assertNotNull(itemRequestUserDto);
        assertEquals(itemRequestUserDto.getId(), newItemRequest.getId());
        assertEquals(itemRequestUserDto.getDescription(), itemRequest.getDescription());
        assertTrue(itemRequestUserDto.getItems().isEmpty());
    }

    @Test
    void findByIdExcepUser() {
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            ItemRequestDto itemRequestDto = mapperItemRequest.toItemRequestDto(itemRequest);
            itemRequestService.create(itemRequestDto, 100L);
        });

        Assertions.assertEquals("Такой пользователь не существует.", thrown.getMessage());
    }

    @Test
    void getAllItemRequest() {
        ItemRequestDto itemRequestDto = mapperItemRequest.toItemRequestDto(itemRequest);
        User requester = userRepository.save(user);
        ItemRequestDto newItemRequest = itemRequestService.create(itemRequestDto, requester.getId());
        User newUser = userRepository.save(user1);
        MyPageRequest myPageRequest = new MyPageRequest(0, 10, Sort.unsorted());

        List<ItemRequestUserDto> findAllItemsRequests = itemRequestService.findAllItemsRequests(myPageRequest, newUser.getId());

        assertFalse(findAllItemsRequests.isEmpty());
        assertEquals(findAllItemsRequests.size(), 1);
        assertEquals(findAllItemsRequests.get(0).getId(), newItemRequest.getId());
        assertEquals(findAllItemsRequests.get(0).getDescription(), newItemRequest.getDescription());
        assertTrue(findAllItemsRequests.get(0).getItems().isEmpty());
    }

    @Test
    void getAllItemRequestEmpty() {
        User requester = userRepository.save(user);
        MyPageRequest myPageRequest = new MyPageRequest(0, 10, Sort.unsorted());

        List<ItemRequestUserDto> findAllItemsRequests = itemRequestService.findAllItemsRequests(myPageRequest, requester.getId());

        assertTrue(findAllItemsRequests.isEmpty());
    }

    @Test
    void findAllItemRequestUser() {
        ItemRequestDto itemRequestDto = mapperItemRequest.toItemRequestDto(itemRequest);
        User requester = userRepository.save(user);
        ItemRequestDto newItemRequest = itemRequestService.create(itemRequestDto, requester.getId());

        List<ItemRequestUserDto> findAllItemsRequests = itemRequestService.findAllItemRequestUser(requester.getId());

        assertFalse(findAllItemsRequests.isEmpty());
        assertEquals(findAllItemsRequests.size(), 1);
        assertEquals(findAllItemsRequests.get(0).getId(), newItemRequest.getId());
        assertEquals(findAllItemsRequests.get(0).getDescription(), newItemRequest.getDescription());
        assertTrue(findAllItemsRequests.get(0).getItems().isEmpty());
    }

    @Test
    void findAllItemRequestUserEmpty() {
        User requester = userRepository.save(user);

        List<ItemRequestUserDto> findAllItemsRequests = itemRequestService.findAllItemRequestUser(requester.getId());

        assertTrue(findAllItemsRequests.isEmpty());
    }

}