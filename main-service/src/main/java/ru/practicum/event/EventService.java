package ru.practicum.event;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.event.model.dto.*;
import ru.practicum.event.model.param.EventParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

    EventFullDto update(Long eventId, UpdateEventAdminRequest updateEvent);

    List<EventShortDto> getAllByInitiator(Long userId, Integer from, Integer size);

    EventFullDto getByIdAndInitiator(Long userId, Long eventId);
    EventFullDto getById(Long id);

    List<EventShortDto> getAll(EventParam param);
}
