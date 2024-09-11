package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.EventState;
import ru.practicum.request.model.RequestEntity;
import ru.practicum.request.model.RequestMapper;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.model.dto.ParticipationRequestDto;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private UserEntity userExistCheck(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("Пользователь не найден"));
    }

    private EventEntity eventExistCheck(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new RuntimeException("Событие не найдено"));
    }

    private RequestEntity requestExistCheck(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new RuntimeException("Запрос не найдена"));
    }

    private void requestNotExistCheck(Long userId, Long eventId) {
        if (requestRepository.findByRequester_idAndEvent_id(userId, eventId).isPresent()) {
            throw new RuntimeException("Запрос уже существует");
        }
    }

    private boolean isRequestsLimitReached(EventEntity event) {
        return event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit();
    }

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        requestNotExistCheck(userId, eventId);
        UserEntity user = userExistCheck(userId);
        EventEntity event = eventExistCheck(eventId);
        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new RuntimeException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new RuntimeException("нельзя участвовать в неопубликованном событии");
        }
        if (isRequestsLimitReached(event)) {
            throw new RuntimeException("достигнут лимит запросов на участие");
        }
        EventState state = !event.getRequestModeration() ? EventState.PUBLISHED : EventState.PENDING;
        RequestEntity request = RequestEntity.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(state)
                .build();
        return RequestMapper.mapToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        UserEntity user = userExistCheck(userId);
        RequestEntity request = requestExistCheck(requestId);
        if (!Objects.equals(userId, request.getRequester().getId())) {
            throw new RuntimeException("Пользователь не создатель запроса");
        }
        if (request.getStatus() == EventState.CANCELED || request.getStatus() == EventState.PUBLISHED) {
            throw new RuntimeException("Некорректный статус запроса");
        }
        request.setStatus(EventState.CANCELED);
        RequestEntity entity = requestRepository.save(request);
        return RequestMapper.mapToParticipationRequestDto(entity);
    }

    @Override
    public EventRequestStatusUpdateResult update(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        UserEntity user = userExistCheck(userId);
        EventEntity event = eventExistCheck(eventId);
        if (!event.getRequestModeration()) {
            throw new RuntimeException("Подтверждение заявок не требуется");
        }
        if (isRequestsLimitReached(event)) {
            throw new RuntimeException("достигнут лимит запросов на участие");
        }

        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();
        List<RequestEntity> requestEntities = requestRepository.findAllByIdIn(request.getRequestIds());
        switch (request.getStatus()) {
            case REJECTED: {
                for (RequestEntity e : requestEntities) {
                    rejectRequest(e);
                    result.getRejectedRequests().add(RequestMapper.mapToParticipationRequestDto(e));
                }
                break;
            }
            case CONFIRMED: {
                for (RequestEntity e : requestEntities) {
                    if (!isRequestsLimitReached(event)) {
                        confirmRequest(e, event);
                        result.getConfirmedRequests().add(RequestMapper.mapToParticipationRequestDto(e));
                    } else {
                        rejectRequest(e);
                        result.getRejectedRequests().add(RequestMapper.mapToParticipationRequestDto(e));
                    }
                }
                break;
            }
        }
        return result;
    }

    private void rejectRequest(RequestEntity entity) {
        if (entity.getStatus() != EventState.PENDING) {
            throw new RuntimeException("статус можно изменить только у заявок, находящихся в состоянии ожидания ");
        }
        entity.setStatus(EventState.CANCELED);
        requestRepository.save(entity);
    }

    private void confirmRequest(RequestEntity entity, EventEntity event) {
        if (entity.getStatus() != EventState.PENDING) {
            throw new RuntimeException("статус можно изменить только у заявок, находящихся в состоянии ожидания ");
        }
        entity.setStatus(EventState.PUBLISHED);
        requestRepository.save(entity);
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
    }
}
