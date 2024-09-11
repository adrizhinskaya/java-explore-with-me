package ru.practicum.request.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.categorie.model.CategoryMapper;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.dto.EventFullDto;
import ru.practicum.event.model.dto.NewEventDto;
import ru.practicum.location.LocationEntity;
import ru.practicum.location.LocationMapper;
import ru.practicum.request.model.dto.ParticipationRequestDto;
import ru.practicum.user.model.UserEntity;
import ru.practicum.user.model.UserMapper;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static RequestEntity mapToRequestEntity(ParticipationRequestDto requestDto, EventEntity event, UserEntity user) {
        return RequestEntity.builder()
                .id(requestDto.getId())
                .created(requestDto.getCreated())
                .event(event)
                .requester(user)
                .status(requestDto.getStatus())
                .build();
    }

    public static ParticipationRequestDto mapToParticipationRequestDto(RequestEntity requestEntity) {
        return ParticipationRequestDto.builder()
                .id(requestEntity.getId())
                .created(requestEntity.getCreated())
                .eventId(requestEntity.getEvent().getId())
                .requesterId(requestEntity.getRequester().getId())
                .status(requestEntity.getStatus())
                .build();
    }

    public static List<ParticipationRequestDto> mapToParticipationRequestDto(Iterable<RequestEntity> requestEntities) {
        List<ParticipationRequestDto> dtos = new ArrayList<>();
        for (RequestEntity entity : requestEntities) {
            dtos.add(mapToParticipationRequestDto(entity));
        }
        return dtos;
    }
}
