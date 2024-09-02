package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.dto.UpdateEventUserRequest;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class EventController {
    EventService eventService;

    @PostMapping("/users/{userId}/events")
    public EventFullDto create(@PathVariable Long userId,
                               @Validated @RequestBody NewEventDto newEventDto) {
        return eventService.create(userId, newEventDto);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @Validated @RequestBody UpdateEventUserRequest updateEvent) {
        return eventService.update(userId, eventId, updateEvent);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                               @Validated @RequestBody UpdateEventAdminRequest updateEvent) {
        return eventService.update(eventId, updateEvent);
    }
}
