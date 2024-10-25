package ru.practicum.request.model;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.practicum.event.model.EventEntity;
import ru.practicum.request.model.dto.ParticipationRequestDto;
import ru.practicum.user.model.UserEntity;

import java.util.ArrayList;
import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@RequiredArgsConstructor
public abstract class RequestMapper {
    public RequestEntity toRequestEntity(ParticipationRequestDto dto, EventEntity event, UserEntity requester) {
        return RequestEntity.builder()
                .id(dto.getId())
                .created(dto.getCreated())
                .event(event)
                .requester(requester)
                .status(dto.getStatus())
                .build();
    }

    public ParticipationRequestDto toParticipationRequestDto(RequestEntity requestEntity) {
        return ParticipationRequestDto.builder()
                .id(requestEntity.getId())
                .created(requestEntity.getCreated())
                .event(requestEntity.getEvent().getId())
                .requester(requestEntity.getRequester().getId())
                .status(requestEntity.getStatus())
                .build();
    }

    public List<ParticipationRequestDto> toParticipationRequestDto(Iterable<RequestEntity> requestEntities) {
        List<ParticipationRequestDto> dtos = new ArrayList<>();
        for(RequestEntity request : requestEntities) {
            dtos.add(toParticipationRequestDto(request));
        }
        return dtos;
    }

    public RequestParam createRequestParam(Long userId, Long eventId) {
        return RequestParam.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
    }
}