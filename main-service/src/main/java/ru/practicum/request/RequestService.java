package ru.practicum.request;

import ru.practicum.request.model.RequestParam;
import ru.practicum.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.model.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto create(Long userId, Long eventId);

    ParticipationRequestDto cancel(Long userId, Long requestId);

    EventRequestStatusUpdateResult update(RequestParam params, EventRequestStatusUpdateRequest request);

    List<ParticipationRequestDto> getAll(Long userId);

    List<ParticipationRequestDto> getAllByEvent(Long userId, Long eventId);
}
