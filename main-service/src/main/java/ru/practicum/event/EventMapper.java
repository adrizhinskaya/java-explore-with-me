package ru.practicum.event;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.categorie.CategoryMapper;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.EventShortDto;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.model.param.AdminEventParam;
import ru.practicum.event.model.param.EventParam;
import ru.practicum.event.model.param.EventUpdateParam;
import ru.practicum.event.model.param.PaginationEventParam;
import ru.practicum.exception.ValidationBadRequestException;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.model.LocationEntity;
import ru.practicum.location.model.dto.Location;
import ru.practicum.user.UserMapper;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.model.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class EventMapper {
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;

    public EventMapper() {
        this.categoryMapper = new CategoryMapper();
        this.userMapper = new UserMapper();
        this.locationMapper = new LocationMapper();
    }

    public EventEntity toEventEntity(NewEventDto dto, CategoryEntity category, UserEntity user, LocationEntity location,
                                     EventState state) {
        return EventEntity.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .initiator(user)
                .location(location)
                .paid(dto.getPaid() == null ? false : dto.getPaid())
                .participantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration() == null ? true : dto.getRequestModeration())
                .state(state)
                .title(dto.getTitle())
                .build();
    }

    public EventFullDto toEventFullDto(EventEntity entity, Long confirmedRequests, Long views) {
        CategoryDto categoryDto = categoryMapper.toCategoryDto(entity.getCategory());
        UserShortDto userShortDto = userMapper.toUserShortDto(entity.getInitiator());
        Location location = locationMapper.toLocation(entity.getLocation());
        return EventFullDto.builder()
                .id(entity.getId())
                .annotation(entity.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(confirmedRequests)
                .createdOn(entity.getCreatedOn())
                .description(entity.getDescription())
                .eventDate(entity.getEventDate())
                .initiator(userShortDto)
                .location(location)
                .paid(entity.getPaid())
                .participantLimit(entity.getParticipantLimit())
                .publishedOn(entity.getPublishedOn())
                .requestModeration(entity.getRequestModeration())
                .state(entity.getState())
                .title(entity.getTitle())
                .views(views)
                .build();
    }

    public EventShortDto toEventShortDto(EventEntity entity, Long confirmedRequests, Long views) {
        CategoryDto categoryDto = categoryMapper.toCategoryDto(entity.getCategory());
        UserShortDto userShortDto = userMapper.toUserShortDto(entity.getInitiator());
        return EventShortDto.builder()
                .id(entity.getId())
                .annotation(entity.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(confirmedRequests)
                .eventDate(entity.getEventDate())
                .initiator(userShortDto)
                .paid(entity.getPaid())
                .title(entity.getTitle())
                .views(views)
                .build();
    }

    public EventUpdateParam createEventUpdateParam(Long userId, Long eventId) {
        return EventUpdateParam.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
    }

    public EventParam createEventParam(String text,
                                       Set<Long> categories,
                                       Boolean paid,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Boolean onlyAvailable,
                                       String sort,
                                       Integer from,
                                       Integer size) {
        validateEventParam(categories, rangeStart, rangeEnd);
        return EventParam.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();
    }

    private void validateEventParam(Set<Long> categories,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd) {
        if (categories != null && !categories.stream().allMatch(id -> id > 0)) {
            throw new ValidationBadRequestException("Some id in categories are not valid.");
        }
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationBadRequestException("rangeStart should be before rangeEnd.");
        }
    }

    public AdminEventParam createAdminEventParam(Set<Long> users,
                                                 Set<EventState> states,
                                                 Set<Long> categories,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 Integer from,
                                                 Integer size) {
        return AdminEventParam.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
    }

    public PaginationEventParam createPaginationEventParam(Integer from, Integer size) {
        return PaginationEventParam.builder()
                .from(from)
                .size(size)
                .build();
    }
}