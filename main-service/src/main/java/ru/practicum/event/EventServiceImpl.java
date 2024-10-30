package ru.practicum.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.categorie.CategoryRepository;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.categorie.model.CategoryMapper;
import ru.practicum.client.StatsClient;
import ru.practicum.event.model.*;
import ru.practicum.event.model.dto.*;
import ru.practicum.event.model.param.AdminEventParam;
import ru.practicum.event.model.param.EventParam;
import ru.practicum.event.model.param.EventUpdateParam;
import ru.practicum.event.model.param.PaginationEventParam;
import ru.practicum.exception.ConstraintConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.ValidationBadRequestException;
import ru.practicum.location.Location;
import ru.practicum.location.LocationEntity;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationRepository;
import ru.practicum.pagination.PaginationHelper;
import ru.practicum.request.RequestRepository;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.model.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ViewsService viewsService;
    private final ConditionBuilder conditionBuilder;
    private final StatsClient statsClient;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;

    private UserEntity userExistCheck(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователь не найден"));
    }

    private CategoryEntity categoryExistCheck(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new EntityNotFoundException("Категория не найдена"));
    }

    private LocationEntity getOrCreateLocation(Location location) {
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElse(locationRepository.save(locationMapper.toLocationEntity(location)));
    }

    private EventEntity eventExistCheck(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Событие не найдено"));
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        UserEntity user = userExistCheck(userId);
        CategoryEntity category = categoryExistCheck(newEventDto.getCategory());
        LocationEntity locationEntity = getOrCreateLocation(newEventDto.getLocation());
        EventEntity entity = eventRepository.save(eventMapper.toEventEntity(newEventDto, category, user, locationEntity, EventState.PENDING));
        return eventMapper.toEventFullDto(entity, 0L, 0L);
    }

    @Override
    public EventFullDto update(EventUpdateParam params, UpdateEventUserRequest updateEvent) {
        UserEntity user = userExistCheck(params.getUserId());
        EventEntity event = eventExistCheck(params.getEventId());
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConstraintConflictException("Опубликованное событие не может быть изменено");
        }
        if (updateEvent.getEventDate() != null && updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationBadRequestException("EventDate must be at least 2 hours from now");
        }

        UserStateAction state = updateEvent.getStateAction();
        if (state != null) {
            switch (state) {
                case CANCEL_REVIEW:
                    if (event.getState().equals(EventState.PENDING)) {
                        event.setState(EventState.CANCELED);
                    } else {
                        throw new ConstraintConflictException("Некорректный stateAction");
                    }
                    break;
                case SEND_TO_REVIEW:
                    if (event.getState().equals(EventState.CANCELED)) {
                        event.setState(EventState.PENDING);
                    } else {
                        throw new ConstraintConflictException("Некорректный stateAction");
                    }
                    break;
            }
        }

        EventEntity entity = eventRepository.save(updateEntityFields(event, updateEvent));
        return eventMapper.toEventFullDto(entity, 0L, 0L);
    }

    @Override
    public EventFullDto update(Long eventId, UpdateEventAdminRequest updateEvent) {
        EventEntity event = eventExistCheck(eventId);
        AdminStateAction state = updateEvent.getStateAction();
        if (state != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new ConstraintConflictException("Invalid stateAction .");
            }
            switch (state) {
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
            }
        }
        EventEntity entity = eventRepository.save(updateEntityFields(event, updateEvent));
        return eventMapper.toEventFullDto(entity, 0L, 0L);
    }

    @Override
    public List<EventShortDto> getAllByInitiator(Long userId, PaginationEventParam params) {
        userExistCheck(userId);
        PaginationHelper<EventEntity> paginationHelper = new PaginationHelper<>(params.getFrom(), params.getSize());
        List<EventEntity> entities = paginationHelper.findAllWithPagination(eventRepository::findAllByInitiatorId, userId);
        return viewsService.toEventShortDtos(entities, false);
    }

    @Override
    public EventFullDto getByIdAndInitiator(Long userId, Long eventId) {
        userExistCheck(userId);
        EventEntity entity = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("События не существует"));
        return eventMapper.toEventFullDto(entity, 0L, 0L);
    }

    @Override
    public EventFullDto getById(Long id) {
        EventEntity entity = eventExistCheck(id);
        if (entity.getState() != EventState.PUBLISHED) {
            throw new EntityNotFoundException("Event should be PUBLISHED");
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

    private EventEntity updateEntityFields(EventEntity event, UpdateEventRequest updated) {
        if (updated.getAnnotation() != null) {
            event.setAnnotation(updated.getAnnotation());
        }
        if (updated.getCategory() != null) {
            CategoryEntity category = categoryExistCheck(updated.getCategory());
            event.setCategory(category);
        }
        if (updated.getDescription() != null) {
            event.setDescription(updated.getDescription());
        }
        if (updated.getEventDate() != null) {
            event.setEventDate(updated.getEventDate());
        }
        if (updated.getLocation() != null) {
            LocationEntity location = getOrCreateLocation(updated.getLocation());
            event.setLocation(location);
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
        Page<EventEntity> nextPage = firstPage.hasNext() ? eventRepository.findAll(finalCondition, ph.getPageRequestForNextPage(sort)) : null;
        return ph.mergePages(firstPage, nextPage);
    }

    private Sort makeSort(EventParam param) {
        return switch (param.getSort()) {
            case "EVENT_DATE" -> Sort.by("eventDate").descending();
            case "VIEWS" -> Sort.by("views").descending();
            default -> Sort.by("publishedOn").ascending();
        };
    }
}
