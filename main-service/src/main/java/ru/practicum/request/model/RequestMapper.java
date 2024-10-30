package ru.practicum.request.model;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.request.model.dto.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class RequestMapper {
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
        for (RequestEntity request : requestEntities) {
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