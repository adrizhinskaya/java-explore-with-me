package ru.practicum.event.model;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.categorie.model.CategoryMapper;
import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.EventShortDto;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.event.model.param.AdminEventParam;
import ru.practicum.event.model.param.EventParam;
import ru.practicum.event.model.param.EventUpdateParam;
import ru.practicum.event.model.param.PaginationEventParam;
import ru.practicum.location.Location;
import ru.practicum.location.LocationEntity;
import ru.practicum.location.LocationMapper;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.model.UserMapper;
import ru.practicum.user.model.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@RequiredArgsConstructor
public class EventMapper {
    public EventEntity toEventEntity(NewEventDto newEventDto, CategoryEntity category, UserEntity user,
                                     LocationEntity location) {
        return EventEntity.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .location(location)
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .build();
    }

    public EventFullDto toEventFullDto(EventEntity eventEntity,
                                       CategoryDto categoryDto,
                                       UserShortDto userShortDto,
                                       Location location,
                                       Long confirmedRequests,
                                       Long views) {
        return EventFullDto.builder()
                .id(eventEntity.getId())
                .annotation(eventEntity.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(confirmedRequests)
                .createdOn(eventEntity.getCreatedOn())
                .description(eventEntity.getDescription())
                .eventDate(eventEntity.getEventDate())
                .initiator(userShortDto)
                .location(location)
                .paid(eventEntity.getPaid())
                .participantLimit(eventEntity.getParticipantLimit())
                .publishedOn(eventEntity.getPublishedOn())
                .requestModeration(eventEntity.getRequestModeration())
                .state(eventEntity.getState())
                .title(eventEntity.getTitle())
                .views(views)
                .build();
    }

    public EventShortDto toEventShortDto(EventEntity eventEntity,
                                         CategoryDto categoryDto,
                                         UserShortDto userShortDto,
                                         Long confirmedRequests,
                                         Long views) {
        return EventShortDto.builder()
                .id(eventEntity.getId())
                .annotation(eventEntity.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(confirmedRequests)
                .eventDate(eventEntity.getEventDate())
                .initiator(userShortDto)
                .paid(eventEntity.getPaid())
                .title(eventEntity.getTitle())
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