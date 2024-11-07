package ru.practicum.categorie;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.categorie.model.dto.NewCategoryDto;
import ru.practicum.logger.ColoredCRUDLogger;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Validated @RequestBody NewCategoryDto newCategoryDto) {
        ColoredCRUDLogger.logPost("MAIN /admin/categories", newCategoryDto.toString());
        CategoryDto result = categoryService.create(newCategoryDto);
        ColoredCRUDLogger.logPostComplete("MAIN /admin/categories", result.toString());
        return result;
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto update(@PathVariable Long catId,
                              @Validated @RequestBody NewCategoryDto newCategoryDto) {
        String url = String.format("MAIN /admin/categories/{%s}", catId);
        ColoredCRUDLogger.logPatch(url, newCategoryDto.toString());
        CategoryDto result = categoryService.update(catId, newCategoryDto);
        ColoredCRUDLogger.logPatchComplete(url, result.toString());
        return result;
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAll(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        String url = String.format("MAIN /categories?{%s}&{%s}", from, size);
        ColoredCRUDLogger.logGet(url);
        List<CategoryDto> result = categoryService.getAll(from, size);
        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
        return result;
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getById(@PathVariable Long catId) {
        String url = String.format("MAIN /categories/{%s}", catId);
        ColoredCRUDLogger.logGet(url);
        CategoryDto result = categoryService.getById(catId);
        ColoredCRUDLogger.logGetComplete(url, result.toString());
        return result;
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        String url = String.format("MAIN /admin/categories/{%s}", catId);
        ColoredCRUDLogger.logDelete(url);
        categoryService.delete(catId);
        ColoredCRUDLogger.logDeleteComplete(url);
    }
}
