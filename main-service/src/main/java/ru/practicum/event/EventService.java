package ru.practicum.event;

import ru.practicum.event.model.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

    EventFullDto update(Long eventId, UpdateEventAdminRequest updateEvent);

    List<EventShortDto> getAllByInitiator(Long userId, Integer from, Integer size);

    EventFullDto getByInitiator(Long userId, Long eventId);

    List<EventShortDto> getAll(String text, Set<Long> categories, Boolean paid,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                      String sort, int from, int size);
}
