package ru.practicum.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.categorie.CategoryRepository;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.dto.*;
import ru.practicum.event.model.enums.AdminStateAction;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.model.enums.UserStateAction;
import ru.practicum.event.model.enums.stateMachine.StateMachine;
import ru.practicum.event.model.param.AdminEventParam;
import ru.practicum.event.model.param.EventParam;
import ru.practicum.event.model.param.EventUpdateParam;
import ru.practicum.event.model.param.PaginationEventParam;
import ru.practicum.exception.ConstraintConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.ValidationBadRequestException;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationRepository;
import ru.practicum.location.model.LocationEntity;
import ru.practicum.location.model.dto.Location;
import ru.practicum.pagination.PaginationHelper;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final ViewsService viewsService;
    private final ConditionBuilder conditionBuilder;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        UserEntity user = userExistCheck(userId);
        CategoryEntity category = categoryExistCheck(newEventDto.getCategory());
        LocationEntity locationEntity = getOrCreateLocation(newEventDto.getLocation());
        EventEntity entity = eventRepository.save(eventMapper.toEventEntity(newEventDto, category, user,
                locationEntity, EventState.PENDING));
        return eventMapper.toEventFullDto(entity, 0L, 0L);
    }

    @Override
    public EventFullDto update(EventUpdateParam params, UpdateEventUserRequest updateEvent) {
        EventEntity event = checkNewEvent(params, updateEvent);
        checkStateAndUpdate(updateEvent.getStateAction(), event);
        EventEntity entity = eventRepository.save(updateEntityFields(event, updateEvent));
        return eventMapper.toEventFullDto(entity, 0L, 0L);
    }

    @Override
    public EventFullDto update(Long eventId, UpdateEventAdminRequest updateEvent) {
        EventEntity event = eventExistCheck(eventId);
        AdminStateAction state = updateEvent.getStateAction();
        checkStateAndUpdate(state, event);
        EventEntity entity = eventRepository.save(updateEntityFields(event, updateEvent));
        return eventMapper.toEventFullDto(entity, 0L, 0L);
    }

    @Override
    public List<EventShortDto> getAllByInitiator(Long userId, PaginationEventParam params) {
        userExistCheck(userId);
        PaginationHelper<EventEntity> paginationHelper = new PaginationHelper<>(params.getFrom(), params.getSize());
        List<EventEntity> entities = paginationHelper
                .findAllWithPagination(eventRepository::findAllByInitiatorId, userId);
        return viewsService.toEventShortDtos(entities, false);
    }

    @Override
    public EventFullDto getByIdAndInitiator(Long userId, Long eventId) {
        userExistCheck(userId);
        EventEntity entity = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Event not exists ."));
        return eventMapper.toEventFullDto(entity, 0L, 0L);
    }

    @Override
    public EventFullDto getById(Long id) {
        EventEntity entity = eventExistCheck(id);
        if (entity.getState() != EventState.PUBLISHED) {
            throw new EntityNotFoundException("Event should be PUBLISHED .");
        }
        return viewsService.toEventFullDtos(null, null, List.of(entity), true).getFirst();
    }

    @Override
    public List<EventShortDto> getAll(EventParam param) {
        BooleanExpression finalCondition = conditionBuilder.makeCondition(param);
        Sort sort = makeSort(param);
        List<EventEntity> entities = getAllWithPagination(param.getFrom(), param.getSize(), finalCondition, sort);
        return viewsService.toEventShortDtos(entities, false);
    }

    @Override
    public List<EventFullDto> getAllFromAdmin(AdminEventParam param) {
        BooleanExpression finalCondition = conditionBuilder.makeCondition(param);
        List<EventEntity> entities = getAllWithPagination(param.getFrom(), param.getSize(), finalCondition, null);
        return viewsService.toEventFullDtos(param.getRangeStart(), param.getRangeEnd(), entities, false);
    }

    private EventEntity checkNewEvent(EventUpdateParam params, UpdateEventUserRequest updateEvent) {
        userExistCheck(params.getUserId());
        EventEntity event = eventExistCheck(params.getEventId());
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConstraintConflictException("Published event can`t be changed .");
        }
        if (updateEvent.getEventDate() != null &&
                updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationBadRequestException("EventDate must be at least 2 hours from now .");
        }
        return event;
    }

    private void checkStateAndUpdate(UserStateAction state, EventEntity event) {
        if (state == null) return;
        StateMachine stateMachine = new StateMachine();
        stateMachine.updateEventState(event, state);
    }

    private void checkStateAndUpdate(AdminStateAction action, EventEntity event) {
        if (action == null) return;
        StateMachine stateMachine = new StateMachine();
        stateMachine.updateEventState(event, action);
    }

    private EventEntity updateEntityFields(EventEntity event, UpdateEventRequest updated) {
        if (updated.getAnnotation() != null) {
            event.setAnnotation(updated.getAnnotation());
        }
        if (updated.getCategory() != null) {
            event.setCategory(categoryExistCheck(updated.getCategory()));
        }
        if (updated.getDescription() != null) {
            event.setDescription(updated.getDescription());
        }
        if (updated.getEventDate() != null) {
            event.setEventDate(updated.getEventDate());
        }
        if (updated.getLocation() != null) {
            event.setLocation(getOrCreateLocation(updated.getLocation()));
        }
        if (updated.getPaid() != null) {
            event.setPaid(updated.getPaid());
        }
        if (updated.getParticipantLimit() != null) {
            event.setParticipantLimit(updated.getParticipantLimit());
        }
        if (updated.getRequestModeration() != null) {
            event.setRequestModeration(updated.getRequestModeration());
        }
        if (updated.getTitle() != null) {
            event.setTitle(updated.getTitle());
        }
        return event;
    }

    private List<EventEntity> getAllWithPagination(int from, int size, BooleanExpression finalCondition, Sort sort) {
        PaginationHelper<EventEntity> ph = new PaginationHelper<>(from, size);
        Page<EventEntity> firstPage = eventRepository.findAll(finalCondition, ph.getPageRequestForFirstPage(sort));
        Page<EventEntity> nextPage = firstPage.hasNext() ? eventRepository.findAll(finalCondition,
                ph.getPageRequestForNextPage(sort)) : null;
        return ph.mergePages(firstPage, nextPage);
    }

    private Sort makeSort(EventParam param) {
        return switch (param.getSort()) {
            case "EVENT_DATE" -> Sort.by("eventDate").descending();
            case "VIEWS" -> Sort.by("views").descending();
            default -> Sort.by("publishedOn").ascending();
        };
    }

    private UserEntity userExistCheck(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found ."));
    }

    private CategoryEntity categoryExistCheck(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new EntityNotFoundException("Category not found ."));
    }

    private LocationEntity getOrCreateLocation(Location location) {
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElse(locationRepository.save(locationMapper.toLocationEntity(location)));
    }

    private EventEntity eventExistCheck(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event not found ."));
    }
}
