package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.MapperItems;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUserDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final MapperItems mapperItems;
    private final MapperItemRequest mapperItemRequest;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        validUser(userId);
        log.debug("Получен Post-запрос на запрос вещи");
        ItemRequest itemRequest = itemRequestRepository.save(mapperItemRequest.toItemRequest(itemRequestDto, userId));
        return mapperItemRequest.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestUserDto> findAllItemsRequests(Long userId, Integer from, Integer size) {
        validUser(userId);
        final MyPageRequest pageRequest = new MyPageRequest(from, size, Sort.unsorted());

        log.debug("Получен Get-запрос на все запросы вещей пользователей от id {}", userId);
        Page<ItemRequest> itemRequests = itemRequestRepository.findAll(pageRequest);
        if (itemRequests.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemRequestUserDto> itemRequestUserDtos = itemRequests.stream()
                .filter(itemRequest -> itemRequest.getRequester() != userId)
                .map(mapperItemRequest::toItemRequestUserDto)
                .collect(Collectors.toList());

        for (ItemRequestUserDto item : itemRequestUserDtos) {
            item.setItems(itemRepository.findAll().stream()
                    .filter(item1 -> Objects.equals(item1.getRequestId(), item.getId()))
                    .map(mapperItems::toItemDto)
                    .collect(Collectors.toList()));
        }
        return itemRequestUserDtos;
    }

    @Override
    public ItemRequestUserDto findById(Long itemRequestId, Long userId) {
        validUser(userId);
        log.debug("Получен Get-запрос на запрос вещи с ответами от пользователя id {}", userId);
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Такой запрос не существует."));
        List<ItemDto> itemDtos = itemRepository.findAll().stream()
                .filter(item -> Objects.equals(item.getRequestId(), itemRequestId))
                .map(mapperItems::toItemDto)
                .collect(Collectors.toList());
        ItemRequestUserDto itemRequestUserDto = mapperItemRequest.toItemRequestUserDto(itemRequest);
        itemRequestUserDto.setItems(itemDtos);
        return itemRequestUserDto;
    }

    @Override
    public List<ItemRequestUserDto> findAllItemRequestUser(Long userId) {
        validUser(userId);
        log.debug("Получен Get-запрос на все запросы вещей пользователя id {}", userId);
        List<ItemRequestUserDto> itemRequests = itemRequestRepository.findAll()
                .stream()
                .filter(itemRequest -> itemRequest.getRequester() == userId)
                .map(mapperItemRequest::toItemRequestUserDto)
                .collect(Collectors.toList());
        if (itemRequests.isEmpty()) {
            return new ArrayList<>();
        }
        for (ItemRequestUserDto itemRequest : itemRequests) {
            itemRequest.setItems(itemRepository.findAll()
                    .stream()
                    .filter(item -> Objects.equals(item.getRequestId(), itemRequest.getId()))
                    .map(mapperItems::toItemDto)
                    .collect(Collectors.toList()));
        }
        return itemRequests;
    }

    private void validUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такой пользователь не существует."));
    }
}
