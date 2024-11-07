package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.model.CompilationEntity;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;
import ru.practicum.compilation.model.dto.UpdateCompilationRequest;
import ru.practicum.event.EventRepository;
import ru.practicum.event.ViewsService;
import ru.practicum.event.model.EventEntity;
import ru.practicum.exception.ConstraintConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.pagination.PaginationHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final ViewsService viewsService;
    private final CompilationRepository compRepo;
    private final EventsCompilationsRepository eventsCompRepo;
    private final EventRepository eventRepo;
    private final CompilationMapper mapper;

    @Override
    public CompilationDto create(NewCompilationDto newCompDto) {
        newCompDto.setPinned(pinnedValidation(newCompDto.getPinned()));
        titleAlreadyExistCheck(newCompDto.getTitle());
        CompilationEntity compilation = compRepo.save(mapper.toCompilationEntity(newCompDto));
        List<EventEntity> events = checkEventsAndAddToCompilations(compilation, newCompDto.getEvents());
        return mapper.createEventCompilationDto(compilation, viewsService.toEventShortDtos(events, false));
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest updatedComp) {
        CompilationEntity compilation = compilationExistCheck(compId);
        updatedComp.setPinned(pinnedValidation(updatedComp.getPinned()));
        mapper.updateEntity(compilation, updatedComp);
        eventsCompRepo.deleteAll(eventsCompRepo.findAllByCompilationId(compId));
        List<EventEntity> events = checkEventsAndAddToCompilations(compilation, updatedComp.getEvents());
        return mapper.createEventCompilationDto(compilation, viewsService.toEventShortDtos(events, false));
    }

    @Override
    public CompilationDto getById(Long compId) {
        CompilationEntity compilation = compilationExistCheck(compId);
        List<Long> eventsIds = eventsCompRepo.findAllEventsByCompilationId(compilation.getId());
        List<EventEntity> events = eventRepo.findAllById(eventsIds);
        return mapper.createEventCompilationDto(compilation, viewsService.toEventShortDtos(events, false));
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        List<CompilationEntity> entities = getAllWithPagination(pinned, from, size);
        List<CompilationDto> result = new ArrayList<>();
        for (CompilationEntity comp : entities) {
            List<Long> eventsIds = eventsCompRepo.findAllEventsByCompilationId(comp.getId());
            List<EventEntity> events = eventRepo.findAllById(eventsIds);
            result.add(mapper.createEventCompilationDto(comp, viewsService.toEventShortDtos(events, false)));
        }
        return result;
    }

    @Override
    public void delete(Long compId) {
        compRepo.deleteById(compId);
    }

    private CompilationEntity compilationExistCheck(Long compId) {
        return compRepo.findById(compId).orElseThrow(() ->
                new EntityNotFoundException("Compilation not found ."));
    }

    private EventEntity eventExistCheck(Long eventId) {
        return eventRepo.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event not found ."));
    }

    private boolean pinnedValidation(Boolean pinned) {
        if (pinned == null) return false;
        return pinned;
    }

    private void titleAlreadyExistCheck(String title) {
        Optional<CompilationEntity> compilation = compRepo.findFirstByTitle(title);
        if (compilation.isPresent()) {
            throw new ConstraintConflictException("Such compilation title already exists .");
        }
    }

    private List<EventEntity> checkEventsAndAddToCompilations(CompilationEntity compilation, List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<EventEntity> events = new ArrayList<>();
        for (Long id : eventIds) {
            EventEntity event = eventExistCheck(id);
            events.add(event);
            eventsCompRepo.save(mapper.createEventCompilationEntity(compilation, event));
        }
        return events;
    }

    private List<CompilationEntity> getAllWithPagination(Boolean pinned, int from, int size) {
        PaginationHelper<CompilationEntity> ph = new PaginationHelper<>(from, size);
        Page<CompilationEntity> firstPage, nextPage;
        if (pinned != null) {
            firstPage = compRepo.findAllByPinned(pinned, ph.getPageRequestForFirstPage(null));
            nextPage = firstPage.hasNext() ?
                    compRepo.findAllByPinned(pinned, ph.getPageRequestForNextPage(null)) : null;
        } else {
            firstPage = compRepo.findAll(ph.getPageRequestForFirstPage(null));
            nextPage = firstPage.hasNext() ? compRepo.findAll(ph.getPageRequestForNextPage(null)) : null;
        }
        return ph.mergePages(firstPage, nextPage);
    }
}
