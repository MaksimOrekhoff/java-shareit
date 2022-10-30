package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


/**
 * TODO Sprint add-item-requests.
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestService;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemsRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all requests items users");
        return itemRequestService.findAllItemsRequests(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsRequestsUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all requests items user{}", userId);
        return itemRequestService.findAllItemRequestUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable long requestId) {
        log.info("Get request item  by id{}", requestId);
        return itemRequestService.findById(requestId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequestItem(userId, itemRequestDto);
    }
}
