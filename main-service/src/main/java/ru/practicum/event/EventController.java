package ru.practicum.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsClient;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.dto.*;
import ru.practicum.event.model.param.AdminEventParam;
import ru.practicum.event.model.param.EventParam;
import ru.practicum.logger.ColoredCRUDLogger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class EventController {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private final StatsClient statsClient;
    private final EventService eventService;
    private final EventMapper eventMapper;
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
        EventFullDto result = eventService.update(eventMapper.createEventUpdateParam(userId, eventId), updateEvent);
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
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        String url = String.format("MAIN /users/{%s}/events?{%s}&{%s}", userId, from, size);
        ColoredCRUDLogger.logGet(url);
        List<EventShortDto> result = eventService.getAllByInitiator(userId, eventMapper.createPaginationEventParam(from, size));
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
    public EventFullDto getById(@PathVariable Long id, HttpServletRequest request) {
        String url = String.format("MAIN /events/{%s}", id);
        ColoredCRUDLogger.logGet(url);
        statsClient.postStats("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        EventFullDto result = eventService.getById(id);
        ColoredCRUDLogger.logGetComplete(url, result.toString());
        return result;
    }

    @GetMapping("/events")
    public List<EventShortDto> getAll(@RequestParam(required = false, name = "text") String text,
                                      @RequestParam(required = false, name = "categories") Set<Long> categories,
                                      @RequestParam(required = false, name = "paid") Boolean paid,
                                      @RequestParam(required = false, name = "rangeStart") String rangeStart,
                                      @RequestParam(required = false, name = "rangeEnd") String rangeEnd,
                                      @RequestParam(required = false, name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                      @RequestParam(required = false, name = "sort", defaultValue = "PUBLISHED_ON") String sort,
                                      @PositiveOrZero @RequestParam(required = false, name = "from", defaultValue = "0") Integer from,
                                      @Positive @RequestParam(required = false, name = "size", defaultValue = "10") Integer size,
                                      HttpServletRequest request) {
        String url = String.format("MAIN /events?{%s}&{%s}&{%s}&{%s}&{%s}&{%s}&{%s}&{%s}&{%s}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        ColoredCRUDLogger.logGet(url);
        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = rangeEnd == null ? null :  LocalDateTime.parse(rangeEnd, formatter);
        statsClient.postStats("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        EventParam param = eventMapper.createEventParam(text, categories, paid, start, end, onlyAvailable, sort, from, size);
        List<EventShortDto> result = eventService.getAll(param);
        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
        return result;
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getAllFromAdmin(@RequestParam(required = false, name = "users") Set<Long> users,
                                              @RequestParam(required = false, name = "states") Set<EventState> states,
                                              @RequestParam(required = false, name = "categories") Set<Long> categories,
                                              @RequestParam(required = false, name = "rangeStart") String rangeStart,
                                              @RequestParam(required = false, name = "rangeEnd") String rangeEnd,
                                              @PositiveOrZero @RequestParam(required = false, name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(required = false, name = "size", defaultValue = "10") Integer size) {
        String url = String.format("MAIN /admin/events?{%s}&{%s}&{%s}&{%s}&{%s}&{%s}&{%s}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        ColoredCRUDLogger.logGet(url);
        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = rangeEnd == null ? null :  LocalDateTime.parse(rangeEnd, formatter);
        AdminEventParam param = eventMapper.createAdminEventParam(users, states, categories, start, end, from, size);
        List<EventFullDto> result = eventService.getAllFromAdmin(param);
        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
        return result;
    }
}