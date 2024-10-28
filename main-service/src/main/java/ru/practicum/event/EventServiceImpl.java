package ru.practicum.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.categorie.CategoryRepository;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.categorie.model.CategoryMapper;
import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ViewStats;
import ru.practicum.event.model.*;
import ru.practicum.event.model.dto.*;
import ru.practicum.event.model.param.AdminEventParam;
import ru.practicum.event.model.param.EventParam;
import ru.practicum.event.model.param.EventUpdateParam;
import ru.practicum.event.model.param.PaginationEventParam;
import ru.practicum.exception.ConstraintConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.location.Location;
import ru.practicum.location.LocationEntity;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationRepository;
import ru.practicum.pagination.PaginationHelper;
import ru.practicum.request.RequestRepository;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.model.UserMapper;
import ru.practicum.user.model.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    private Map<Long, Long> getEventsViewsMap(@Nullable LocalDateTime start, @Nullable LocalDateTime end, List<EventEntity> events, Boolean unique) {
        List<String> itemUris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        String startFormatter = start != null ? start.format(formatter) : "1970-01-01 00:00:00";
        String endFormatter = end != null ? end.format(formatter) : "9999-12-31 23:59:59" ;
        List<ViewStats> stats = statsClient.getStats(startFormatter, endFormatter, itemUris, unique);

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> extractIdFromUri(stat.getUri()), // Извлекаем id из uri
                        ViewStats::getHits // Получаем количество просмотров
                ));
    }

    private List<EventShortDto> createEventShortDto(@Nullable LocalDateTime start, @Nullable LocalDateTime end, List<EventEntity> events, Boolean unique) {
        Map<Long, Long> hitsMap = getEventsViewsMap(start, end, events, unique);
        return events.stream()
                .map(event -> {
                    Long confirmedRequests = requestRepository.countAllByEventIdAndStatusIs(event.getId(), RequestStatus.CONFIRMED);
                    Long views = hitsMap.get(event.getId()); // Получаем количество просмотров для текущего события
                    CategoryDto categoryDto = categoryMapper.toCategoryDto(event.getCategory());
                    UserShortDto userShortDto = userMapper.toUserShortDto(event.getInitiator());
                    return eventMapper.toEventShortDto(event, categoryDto, userShortDto, confirmedRequests, views != null ? views : 0L); // Если нет просмотров, ставим 0
                })
                .collect(Collectors.toList());
    }

    private List<EventFullDto> createEventFullDto(@Nullable LocalDateTime start, @Nullable LocalDateTime end, List<EventEntity> events, Boolean unique) {
        Map<Long, Long> hitsMap = getEventsViewsMap(start, end, events, unique);
        return events.stream()
                .map(event -> {
                    Long confirmedRequests = requestRepository.countAllByEventIdAndStatusIs(event.getId(), RequestStatus.CONFIRMED);
                    Long views = hitsMap.get(event.getId()); // Получаем количество просмотров для текущего события
                    CategoryDto categoryDto = categoryMapper.toCategoryDto(event.getCategory());
                    UserShortDto userShortDto = userMapper.toUserShortDto(event.getInitiator());
                    Location location = locationMapper.toLocation(event.getLocation());
                    return eventMapper.toEventFullDto(event, categoryDto, userShortDto, location, confirmedRequests, views != null ? views : 0L); // Если нет просмотров, ставим 0
                })
                .collect(Collectors.toList());
    }

    private Long extractIdFromUri(String uri) {
        // Отбрасываем часть строки "/events/" и парсим id
        String idString = uri.replace("/events/", "");
        return Long.valueOf(idString);
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        UserEntity user = userExistCheck(userId);
        CategoryEntity category = categoryExistCheck(newEventDto.getCategory());
        LocationEntity locationEntity = getOrCreateLocation(newEventDto.getLocation());
        EventEntity entity = eventRepository.save(eventMapper.toEventEntity(newEventDto, category, user, locationEntity, EventState.PENDING));
        CategoryDto categoryDto = categoryMapper.toCategoryDto(entity.getCategory());
        UserShortDto userShortDto = userMapper.toUserShortDto(entity.getInitiator());
        Location location = locationMapper.toLocation(entity.getLocation());
        return eventMapper.toEventFullDto(entity, categoryDto, userShortDto, location, 0L, 0L);
    }

    @Override
    public EventFullDto update(EventUpdateParam params, UpdateEventUserRequest updateEvent) {
        UserEntity user = userExistCheck(params.getUserId());
        EventEntity event = eventExistCheck(params.getEventId());
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConstraintConflictException("Опубликованное событие не может быть изменено");
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
        CategoryDto categoryDto = categoryMapper.toCategoryDto(entity.getCategory());
        UserShortDto userShortDto = userMapper.toUserShortDto(entity.getInitiator());
        Location location = locationMapper.toLocation(entity.getLocation());
        return eventMapper.toEventFullDto(entity, categoryDto, userShortDto, location, 0L, 0L);
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
        CategoryDto categoryDto = categoryMapper.toCategoryDto(entity.getCategory());
        UserShortDto userShortDto = userMapper.toUserShortDto(entity.getInitiator());
        Location location = locationMapper.toLocation(entity.getLocation());
        return eventMapper.toEventFullDto(entity, categoryDto, userShortDto, location, 0L, 0L);
    }

    @Override
    public List<EventShortDto> getAllByInitiator(Long userId, PaginationEventParam params) {
        userExistCheck(userId);
        PaginationHelper<EventEntity> paginationHelper = new PaginationHelper<>(params.getFrom(), params.getSize());
        List<EventEntity> entities = paginationHelper.findAllWithPagination(eventRepository::findAllByInitiatorId, userId);
        return createEventShortDto(null, null, entities, false);
    }

    @Override
    public EventFullDto getByIdAndInitiator(Long userId, Long eventId) {
        userExistCheck(userId);
        EventEntity entity = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("События не существует"));

        CategoryDto categoryDto = categoryMapper.toCategoryDto(entity.getCategory());
        UserShortDto userShortDto = userMapper.toUserShortDto(entity.getInitiator());
        Location location = locationMapper.toLocation(entity.getLocation());
        return eventMapper.toEventFullDto(entity, categoryDto, userShortDto, location, 0L, 0L);
        //return createEventFullDto(null, null, List.of(entity), false).getFirst();
    }

    @Override
    public EventFullDto getById(Long id) {
        EventEntity entity = eventExistCheck(id);
        if(entity.getState() != EventState.PUBLISHED) {
            throw new EntityNotFoundException("Event should be PUBLISHED");
        }
        return createEventFullDto(null, null, List.of(entity), true).getFirst();
    }

    @Override
    public List<EventShortDto> getAll(EventParam param) {
        BooleanExpression finalCondition = makeCondition(param);
        Sort sort = makeSort(param);
        List<EventEntity> entities = getAllWithPagination(param.getFrom(), param.getSize(), finalCondition, sort);
        return createEventShortDto(param.getRangeStart(), param.getRangeEnd(), entities, false);
    }

    @Override
    public List<EventFullDto> getAllFromAdmin(AdminEventParam param) {
        BooleanExpression finalCondition = makeCondition(param);
        List<EventEntity> entities = getAllWithPagination(param.getFrom(), param.getSize(), finalCondition, null);
        return createEventFullDto(param.getRangeStart(), param.getRangeEnd(), entities, false);
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

    private void addTimeCondition(LocalDateTime rangeStart, LocalDateTime rangeEnd, List<BooleanExpression> conditions) {
        QEventEntity event = QEventEntity.eventEntity;
        if (rangeStart != null && rangeEnd != null) {
            conditions.add(event.eventDate.between(rangeStart, rangeEnd));
        }
        if (rangeStart != null) {
            conditions.add(event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            conditions.add(event.eventDate.before(rangeEnd));
        }
    }

    private BooleanExpression makeCondition(AdminEventParam param) {
        QEventEntity event = QEventEntity.eventEntity;
        List<BooleanExpression> conditions = new ArrayList<>();
        //conditions.add(event.state.eq(EventState.PUBLISHED));
        addTimeCondition(param.getRangeStart(), param.getRangeEnd(), conditions);

        if (param.getUsers() != null) {
            conditions.add(QEventEntity.eventEntity.initiator.id.in(param.getUsers()));
        }
        if (param.getStates() != null) {
            conditions.add(QEventEntity.eventEntity.state.in(param.getStates()));
        }
        if (param.getCategories() != null) {
            conditions.add(QEventEntity.eventEntity.category.id.in(param.getCategories()));
        }

        if (conditions.isEmpty()) {
            conditions.add(QEventEntity.eventEntity.isNotNull());
        }

        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    private BooleanExpression makeCondition(EventParam param) {
        QEventEntity event = QEventEntity.eventEntity;
        List<BooleanExpression> conditions = new ArrayList<>();

        conditions.add(event.state.eq(EventState.PUBLISHED));
        addTextCondition(param, conditions);
        addCategoriesCondition(param, conditions);
        addPaidCondition(param, conditions);
        addTimeCondition(param.getRangeStart(), param.getRangeEnd(), conditions);
//        conditions.add(makeTimeCondition(param.getRangeStart(), param.getRangeEnd()));
//        addOnlyAvailableCondition(param, conditions);

        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    private List<EventEntity> getAllWithPagination(int from, int size, BooleanExpression finalCondition, Sort sort) {
        PaginationHelper<EventEntity> ph = new PaginationHelper<>(from, size);
        Page<EventEntity> firstPage = eventRepository.findAll(finalCondition, ph.getPageRequestForFirstPage(sort));
        Page<EventEntity> nextPage = firstPage.hasNext() ? eventRepository.findAll(finalCondition, ph.getPageRequestForNextPage(sort)) : null;
        return ph.mergePages(firstPage, nextPage);
    }

    private void addPaidCondition(EventParam param, List<BooleanExpression> conditions) {
        if (param.getPaid() != null) {
            conditions.add(QEventEntity.eventEntity.paid.eq(param.getPaid()));
        }
    }

//    private void addOnlyAvailableCondition(EventParam param, List<BooleanExpression> conditions) {
//        if (param.getOnlyAvailable()) {
//            conditions.add((QEventEntity.eventEntity.confirmedRequests.eq(QEventEntity.eventEntity.participantLimit)
//                    .or(QEventEntity.eventEntity.participantLimit.eq(0))));
//        }
//
//        conditions.add(QEventEntity.eventEntity.participantLimit < QEventEntity.eventEntity.)
//    }

    private void addCategoriesCondition(EventParam param, List<BooleanExpression> conditions) {
        if (param.getCategories() != null) {
            conditions.add((QEventEntity.eventEntity.category.id.in(param.getCategories())));
        }
    }

    private void addTextCondition(EventParam param, List<BooleanExpression> conditions) {
        if (param.getText() != null) {
            conditions.add((QEventEntity.eventEntity.annotation.containsIgnoreCase(param.getText())
                    .or(QEventEntity.eventEntity.description.containsIgnoreCase(param.getText()))));
        }
    }

    private Sort makeSort(EventParam param) {
        return switch (param.getSort()) {
            case "EVENT_DATE" -> Sort.by("eventDate").descending();
            case "VIEWS" -> Sort.by("views").descending();
            default -> Sort.by("publishedOn").ascending();
        };
    }
}
