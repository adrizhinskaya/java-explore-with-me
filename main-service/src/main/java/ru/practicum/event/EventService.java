package ru.practicum.event;

import ru.practicum.event.model.dto.*;
import ru.practicum.event.model.param.AdminEventParam;
import ru.practicum.event.model.param.EventParam;
import ru.practicum.event.model.param.EventUpdateParam;
import ru.practicum.event.model.param.PaginationEventParam;

import java.util.List;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto update(EventUpdateParam params, UpdateEventUserRequest updateEvent);

    EventFullDto update(Long eventId, UpdateEventAdminRequest updateEvent);

    List<EventShortDto> getAllByInitiator(Long userId, PaginationEventParam params);

    EventFullDto getByIdAndInitiator(Long userId, Long eventId);

    EventFullDto getById(Long id);

    List<EventShortDto> getAll(EventParam param);

    List<EventFullDto> getAllFromAdmin(AdminEventParam param);
}
