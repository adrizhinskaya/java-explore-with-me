package ru.practicum.compilation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.model.CompilationMapper;
import ru.practicum.compilation.model.dto.CompilationDto;
import ru.practicum.compilation.model.dto.NewCompilationDto;
import ru.practicum.compilation.model.dto.UpdateCompilationRequest;
import ru.practicum.logger.ColoredCRUDLogger;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Validated @RequestBody NewCompilationDto newCompilationDto) {
        String url = "MAIN /admin/compilations";
        ColoredCRUDLogger.logPost(url, newCompilationDto.toString());
        CompilationDto result = compilationService.create(newCompilationDto);
        ColoredCRUDLogger.logPostComplete(url, result.toString());
        return result;
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto update(@PathVariable Long compId,
                                 @Validated @RequestBody UpdateCompilationRequest updatedComp) {
        String url = String.format("MAIN /admin/compilations/{%s}", compId);
        ColoredCRUDLogger.logPatch(url, updatedComp.toString());
        CompilationDto result = compilationService.update(compId, updatedComp);
        ColoredCRUDLogger.logPatchComplete(url, result.toString());
        return result;
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getById(@PathVariable Long compId) {
        String url = String.format("MAIN /compilations/{%s}", compId);
        ColoredCRUDLogger.logGet(url);
        CompilationDto result = compilationService.getById(compId);
        ColoredCRUDLogger.logGetComplete(url, result.toString());
        return result;
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getAll(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        String url = String.format("MAIN /compilations?{%s}&{%s}&{%s}", pinned, from, size);
        ColoredCRUDLogger.logGet(url);
        List<CompilationDto> result = compilationService.getAll(pinned, from, size);
        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
        return result;
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        String url = String.format("MAIN /admin/compilations/{%s}", compId);
        ColoredCRUDLogger.logDelete(url);
        compilationService.delete(compId);
        ColoredCRUDLogger.logDeleteComplete(url);
    }
}
