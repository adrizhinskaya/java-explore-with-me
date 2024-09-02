package ru.practicum.event.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.categorie.model.CategoryMapper;
import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.location.LocationEntity;
import ru.practicum.location.LocationMapper;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.model.UserMapper;
import ru.practicum.user.model.dto.UserShortDto;

import java.util.ArrayList;
import java.util.List;

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

    public static EventFullDto mapToEventDto(EventEntity eventEntity) {
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

    public static List<EventFullDto> mapToEventDto(Iterable<EventEntity> eventEntities) {
        List<EventFullDto> dtos = new ArrayList<>();
        for (EventEntity entity : eventEntities) {
            dtos.add(mapToEventDto(entity));
        }
        return dtos;
    }
}
