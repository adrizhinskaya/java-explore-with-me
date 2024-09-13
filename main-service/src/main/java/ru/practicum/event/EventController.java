package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.model.dto.*;
import ru.practicum.event.model.param.EventParam;
import ru.practicum.logger.ColoredCRUDLogger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId,
                               @Validated @RequestBody NewEventDto newEventDto) {
        String url = String.format("MAIN /users/{%s}/events", userId);
        ColoredCRUDLogger.logPost(url, newEventDto.toString());
        EventFullDto result = eventService.create(userId, newEventDto);
        ColoredCRUDLogger.logPostComplete(url, result.toString());
        return result;
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @Validated @RequestBody UpdateEventUserRequest updateEvent) {
        String url = String.format("MAIN /users/{%s}/events/{%s}", userId, eventId);
        ColoredCRUDLogger.logPatch(url, updateEvent.toString());
        EventFullDto result = eventService.update(userId, eventId, updateEvent);
        ColoredCRUDLogger.logPatchComplete(url, result.toString());
        return result;
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                               @Validated @RequestBody UpdateEventAdminRequest updateEvent) {
        String url = String.format("MAIN /admin/events/{%s}", eventId);
        ColoredCRUDLogger.logPatch(url, updateEvent.toString());
        EventFullDto result = eventService.update(eventId, updateEvent);
        ColoredCRUDLogger.logPatchComplete(url, result.toString());
        return result;
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getAllByInitiator(@PathVariable Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @RequestParam(name = "size", defaultValue = "10") int size) {
        String url = String.format("MAIN /users/{%s}/events?{%s}&{%s}", userId, from, size);
        ColoredCRUDLogger.logGet(url);
        List<EventShortDto> result = eventService.getAllByInitiator(userId, from, size);
        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
        return result;
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getByIdAndInitiator(@PathVariable Long userId,
                                       @PathVariable Long eventId) {
        String url = String.format("MAIN /users/{%s}/events/{%s}", userId, eventId);
        ColoredCRUDLogger.logGet(url);
        EventFullDto result = eventService.getByIdAndInitiator(userId, eventId);
        ColoredCRUDLogger.logGetComplete(url, result.toString());
        return result;
    }

    @GetMapping("/events/{id}")
    public EventFullDto getById(@PathVariable Long id) {
        String url = String.format("MAIN /events/{%s}", id);
        ColoredCRUDLogger.logGet(url);
        EventFullDto result = eventService.getById(id);
        ColoredCRUDLogger.logGetComplete(url, result.toString());
        return result;
    }

    @GetMapping("/events")
    public List<EventShortDto> getAll(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) Set<Long> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false) LocalDateTime rangeStart,
                                      @RequestParam(required = false) LocalDateTime rangeEnd,
                                      @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                      @RequestParam(required = false) String sort,
                                      @RequestParam(required = false, defaultValue = "0") int from,
                                      @RequestParam(required = false, defaultValue = "10") int size) {
        String url = String.format("MAIN /events?{%s}&{%s}&{%s}&{%s}&{%s}&{%s}&{%s}&{%s}&{%s}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        ColoredCRUDLogger.logGet(url);
        EventParam param = EventMapper.mapToEventParam(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        List<EventShortDto> result = eventService.getAll(param);
        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
        return result;
    }
}
