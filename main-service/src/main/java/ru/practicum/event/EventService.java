package ru.practicum.event;

import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.event.model.*;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.dto.UpdateEventUserRequest;
import ru.practicum.user.model.UserEntity;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto newEventDto);
    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEvent);
    EventFullDto update(Long eventId, UpdateEventAdminRequest updateEvent);
}
