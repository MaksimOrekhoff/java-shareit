package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestUserDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping("/all")
    public List<ItemRequestUserDto> getAllItemsRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all requests items users");
        return itemRequestService.findAllItemsRequests(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestUserDto> getAllItemsRequestsUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all requests items user{}", userId);
        return itemRequestService.findAllItemRequestUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestUserDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable long requestId) {
        log.info("Get request item  by id{}", requestId);
        return itemRequestService.findById(requestId, userId);
    }

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(itemRequestDto, userId);
    }
}
