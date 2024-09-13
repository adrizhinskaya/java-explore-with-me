package ru.practicum.event.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.categorie.model.CategoryMapper;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.EventShortDto;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.event.model.param.EventParam;
import ru.practicum.location.LocationEntity;
import ru.practicum.location.LocationMapper;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.model.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    public static EventEntity mapToEventEntity(NewEventDto newEventDto, CategoryEntity category, UserEntity user,
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

    public static EventFullDto mapToEventFullDto(EventEntity eventEntity) {
        return EventFullDto.builder()
                .id(eventEntity.getId())
                .annotation(eventEntity.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(eventEntity.getCategory()))
                .confirmedRequests(eventEntity.getConfirmedRequests())
                .createdOn(eventEntity.getCreatedOn())
                .description(eventEntity.getDescription())
                .eventDate(eventEntity.getEventDate())
                .initiator(UserMapper.mapToUserShortDto(eventEntity.getInitiator()))
                .location(LocationMapper.mapToLocation(eventEntity.getLocation()))
                .paid(eventEntity.getPaid())
                .participantLimit(eventEntity.getParticipantLimit())
                .publishedOn(eventEntity.getPublishedOn())
                .requestModeration(eventEntity.getRequestModeration())
                .state(eventEntity.getState())
                .title(eventEntity.getTitle())
                .views(eventEntity.getViews())
                .build();
    }

    public static List<EventFullDto> mapToEventFullDto(Iterable<EventEntity> eventEntities) {
        List<EventFullDto> dtos = new ArrayList<>();
        for (EventEntity entity : eventEntities) {
            dtos.add(mapToEventFullDto(entity));
        }
        return dtos;
    }


    public static EventShortDto mapToEventShortDto(EventEntity eventEntity) {
        return EventShortDto.builder()
                .id(eventEntity.getId())
                .annotation(eventEntity.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(eventEntity.getCategory()))
                .confirmedRequests(eventEntity.getConfirmedRequests())
                .eventDate(eventEntity.getEventDate())
                .initiator(UserMapper.mapToUserShortDto(eventEntity.getInitiator()))
                .paid(eventEntity.getPaid())
                .title(eventEntity.getTitle())
                .views(eventEntity.getViews())
                .build();
    }

    public static List<EventShortDto> mapToEventShortDto(Iterable<EventEntity> eventEntities) {
        List<EventShortDto> dtos = new ArrayList<>();
        for (EventEntity entity : eventEntities) {
            dtos.add(mapToEventShortDto(entity));
        }
        return dtos;
    }

    public static EventParam mapToEventParam(String text,
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
                .onlyAvailable(onlyAvailable != null ? onlyAvailable : false)
                .sort(sort)
                .from(from != null ? from : 0)
                .size(size != null ? size : 10)
                .build();
    }
}
