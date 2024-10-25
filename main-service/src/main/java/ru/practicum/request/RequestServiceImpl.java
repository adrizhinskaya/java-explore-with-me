package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.EventState;
import ru.practicum.exception.ConstraintConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.request.model.RequestEntity;
import ru.practicum.request.model.RequestMapper;
import ru.practicum.request.model.RequestParam;
import ru.practicum.request.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.model.dto.ParticipationRequestDto;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.UserEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper mapper;

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        Map<String, Object> obj = createChecks(userId, eventId);
        UserEntity user = (UserEntity) obj.get("user");
        EventEntity event = (EventEntity) obj.get("event");
        EventState state = event.getRequestModeration() ? EventState.PENDING : EventState.PUBLISHED;
        RequestEntity request = RequestEntity.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(state)
                .build();
        RequestEntity re = requestRepository.save(request);
        ParticipationRequestDto pr = mapper.toParticipationRequestDto(re);
        return pr;
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        RequestEntity request = cancelChecks(userId, requestId);
        if (request.getStatus() == EventState.CANCELED) {
            return mapper.toParticipationRequestDto(request);
        }
        request.setStatus(EventState.CANCELED);
        RequestEntity entity = requestRepository.save(request);
        return mapper.toParticipationRequestDto(entity);
    }

    @Override
    public EventRequestStatusUpdateResult update(RequestParam params, EventRequestStatusUpdateRequest request) {
        Map<String, Object> obj = updateChecks(params.getUserId(), params.getEventId());
        EventEntity event = (EventEntity) obj.get("event");

        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        List<RequestEntity> requestEntities = requestRepository.findAllByIdIn(request.getRequestIds());
        switch (request.getStatus()) {
            case REJECTED: {
                rejectAllRequests(requestEntities, result);
                break;
            }
            case CONFIRMED: {
                confirmAllRequests(requestEntities, result, event);
                break;
            }
        }
        return result;
    }

    @Override
    public List<ParticipationRequestDto> getAll(Long userId) {
        userExistCheck(userId);
        return mapper.toParticipationRequestDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public List<ParticipationRequestDto> getAllByEvent(Long userId, Long eventId) {
        userExistCheck(userId);
        EventEntity event = eventExistCheck(eventId);
        userIsInitiatorCheck(userId, event, true);
        List<RequestEntity> requests = requestRepository.findByInitiatorIdAndEventId(userId, eventId);
        return mapper.toParticipationRequestDto(requests);
    }

    private UserEntity userExistCheck(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found"));
    }

    private EventEntity eventExistCheck(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event not found"));
    }

    private RequestEntity requestExistCheck(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException("Request not found"));
    }

    private void requestsLimitReachedCheck(EventEntity event) {
        Long confirmedRequestsSum = requestRepository.countAllByEventIdAndStatusIs(event.getId(), EventState.PUBLISHED);
        if ((event.getParticipantLimit() != 0 && confirmedRequestsSum == (long) event.getParticipantLimit())) {
            throw new RuntimeException("Limit of participation requests reached");
        }
    }

    private boolean isRequestsLimitReachedCheck(EventEntity event) {
        Long confirmedRequestsSum = requestRepository.countAllByEventIdAndStatusIs(event.getId(), EventState.PUBLISHED);
        return (event.getParticipantLimit() != 0 && confirmedRequestsSum == (long) event.getParticipantLimit());
    }

    private void userIsInitiatorCheck(Long userId, EventEntity event, boolean shouldUserBeInitiator) {
        if (Objects.equals(userId, event.getInitiator().getId())) {
            if (!shouldUserBeInitiator) {
                throw new ConstraintConflictException("User should not be event initiator");
            }
        } else {
            if (shouldUserBeInitiator) {
                throw new ConstraintConflictException("User should be event initiator");
            }
        }
    }

    private Map<String, Object> createChecks(Long userId, Long eventId) {
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConstraintConflictException("Request already exists");
        }
        UserEntity user = userExistCheck(userId);
        EventEntity event = eventExistCheck(eventId);
        userIsInitiatorCheck(userId, event, false);
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConstraintConflictException("Cannot participate in an unpublished event");
        }
        requestsLimitReachedCheck(event);
        return Map.of("user", user,
                "event", event);
    }

    private RequestEntity cancelChecks(Long userId, Long requestId) {
        userExistCheck(userId);
        RequestEntity request = requestExistCheck(requestId);
        if (!Objects.equals(userId, request.getRequester().getId())) {
            throw new ConstraintConflictException("User is not the requester");
        }
        return request;
    }

    private Map<String, Object> updateChecks(Long userId, Long eventId) {
        UserEntity user = userExistCheck(userId);
        EventEntity event = eventExistCheck(eventId);
        userIsInitiatorCheck(userId, event, true);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConstraintConflictException("Confirmation of requests is not required");
        }
        requestsLimitReachedCheck(event);
        return Map.of("user", user,
                "event", event);
    }

    private void rejectAllRequests(List<RequestEntity> requestEntities, EventRequestStatusUpdateResult result) {
        for (RequestEntity r : requestEntities) {
            changeRequestStatus(r, EventState.CANCELED);
        }
        result.setRejectedRequests((mapper.toParticipationRequestDto(requestEntities)));
    }

    private void confirmAllRequests(List<RequestEntity> requestEntities, EventRequestStatusUpdateResult result,
                                    EventEntity event) {
        List<RequestEntity> confirmed = new ArrayList<>();
        for (RequestEntity r : requestEntities) {
            if (isRequestsLimitReachedCheck(event)) {
                requestEntities.removeAll(confirmed);
                result.setConfirmedRequests((mapper.toParticipationRequestDto(confirmed)));
                rejectAllRequests(requestEntities, result);
                return;
            } else {
                changeRequestStatus(r, EventState.PUBLISHED);
                confirmed.add(r);
            }
        }
        result.setConfirmedRequests((mapper.toParticipationRequestDto(confirmed)));
    }

    private void changeRequestStatus(RequestEntity entity, EventState status) {
        if (entity.getStatus() != EventState.PENDING) {
            throw new ConstraintConflictException(
                    "The status can only be changed for requests that are in a pending state");
        }
        entity.setStatus(status);
        requestRepository.save(entity);
    }
}