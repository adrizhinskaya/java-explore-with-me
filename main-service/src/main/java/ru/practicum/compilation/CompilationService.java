package ru.practicum.compilation;

import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;
import ru.practicum.compilation.model.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto update(Long compId, UpdateCompilationRequest updatedComp);

    CompilationDto getById(Long compId);

    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    void delete(Long compId);
}