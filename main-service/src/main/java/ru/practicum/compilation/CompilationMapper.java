package ru.practicum.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.compilation.model.CompilationEntity;
import ru.practicum.compilation.model.EventCompilationEntity;
import ru.practicum.compilation.model.EventCompilationId;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;
import ru.practicum.compilation.model.dto.UpdateCompilationRequest;
import ru.practicum.event.model.EventEntity;
import ru.practicum.event.model.dto.EventShortDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class CompilationMapper {

    public CompilationEntity toCompilationEntity(NewCompilationDto newEventDto) {
        return CompilationEntity.builder()
                .title(newEventDto.getTitle())
                .pinned(newEventDto.getPinned())
                .build();
    }

    public EventCompilationEntity createEventCompilationEntity(CompilationEntity compilation, EventEntity event) {
        EventCompilationId id = new EventCompilationId(compilation.getId(), event.getId());
        return EventCompilationEntity.builder()
                .id(id)
                .compilation(compilation)
                .event(event)
                .build();
    }

    public CompilationDto createEventCompilationDto(CompilationEntity entity, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .pinned(entity.getPinned())
                .events(events)
                .build();
    }

    public void updateEntity(CompilationEntity entity, UpdateCompilationRequest newComp) {
        if (newComp.getTitle() != null) {
            entity.setTitle(newComp.getTitle());
        }
        if (newComp.getPinned() != null) {
            entity.setPinned(newComp.getPinned());
        }
    }
}
