package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.categorie.CategoryRepository;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.event.model.*;
import ru.practicum.event.model.dto.*;
import ru.practicum.location.Location;
import ru.practicum.location.LocationEntity;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationRepository;
import ru.practicum.pagination.PaginationHelper;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    private UserEntity userExistCheck(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("Пользователь не найден"));
    }

    private CategoryEntity categoryExistCheck(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new RuntimeException("Категория не найдена"));
    }

    private LocationEntity getOrCreateLocation(Location location) {
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElse(locationRepository.save(LocationMapper.mapToLocationEntity(location)));
    }

    private EventEntity eventExistCheck(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new RuntimeException("Событие не найдено"));
    }

    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        UserEntity user = userExistCheck(userId);
        CategoryEntity category = categoryExistCheck(newEventDto.getCategory());
        LocationEntity location = getOrCreateLocation(newEventDto.getLocation());
        EventEntity entity = eventRepository.save(EventMapper.mapToEventEntity(newEventDto, category, user, location));
        return EventMapper.mapToEventFullDto(entity);
    }

    @Override
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        UserEntity user = userExistCheck(userId);
        EventEntity event = eventExistCheck(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new RuntimeException("Опубликованное событие не может быть изменено");
        }
        UserStateAction state = updateEvent.getStateAction();
        if (state != null) {
            switch (state) {
                case CANCEL_REVIEW:
                    if (event.getState().equals(EventState.PENDING)) {
                        event.setState(EventState.CANCELED);
                    } else {
                        throw new RuntimeException("Некорректный stateAction");
                    }
                    break;
                case SEND_TO_REVIEW:
                    if (event.getState().equals(EventState.CANCELED)) {
                        event.setState(EventState.PENDING);
                    } else {
                        throw new RuntimeException("Некорректный stateAction");
                    }
                    break;
            }
        }

        EventEntity entity = eventRepository.save(updateEntityFields(event, updateEvent));
        return EventMapper.mapToEventFullDto(entity);
    }

    @Override
    public EventFullDto update(Long eventId, UpdateEventAdminRequest updateEvent) {
        EventEntity event = eventExistCheck(eventId);
        AdminStateAction state = updateEvent.getStateAction();
        if (state != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new RuntimeException("Некорректный stateAction");
            }
            switch (state) {
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    break;
            }
        }
        EventEntity entity = eventRepository.save(updateEntityFields(event, updateEvent));
        return EventMapper.mapToEventFullDto(entity);
    }

    @Override
    public List<EventShortDto> getAllByInitiator(Long userId, Integer from, Integer size) {
        userExistCheck(userId);
        PaginationHelper<EventEntity> paginationHelper = new PaginationHelper<>(from, size);
        List<EventEntity> entities = paginationHelper.findAllWithPagination(eventRepository::findAllByInitiatorId, userId);
        return EventMapper.mapToEventShortDto(entities);
    }

    @Override
    public EventFullDto getByInitiator(Long userId, Long eventId) {
        userExistCheck(userId);
        EventEntity entity = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new RuntimeException("События не существует"));
        return EventMapper.mapToEventFullDto(entity);
    }

    @Override
    public List<EventShortDto> getAll(String text, Set<Long> categories, Boolean paid,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                      String sort, int from, int size) {
        PaginationHelper<EventEntity> paginationHelper = new PaginationHelper<>(from, size);
        List<EventEntity> entities = paginationHelper.findAllWithPagination(eventRepository::findAll);
        return EventMapper.mapToEventShortDto(entities);
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
}
