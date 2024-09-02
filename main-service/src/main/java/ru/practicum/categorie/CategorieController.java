package ru.practicum.categorie;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.categorie.model.dto.NewCategoryDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategorieController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    public CategoryDto create(@Validated NewCategoryDto newCategoryDto) {
        CategoryDto c = categoryService.create(newCategoryDto);
        return c;
        // ДОДЕЛАТЬ РЕАЛИЗАЦИЮ ВОЗВРАТА ТЕЛА ОШИБКИ
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAll(@RequestParam(name = "from", defaultValue = "0") int from,
                                    @RequestParam(name = "size", defaultValue = "10") int size) {
        return categoryService.getAll(from, size);
        // ДОДЕЛАТЬ РЕАЛИЗАЦИЮ ВОЗВРАТА ТЕЛА ОШИБКИ
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getById(@PathVariable(name = "catId") Long catId) {
        return categoryService.getById(catId);
        // ДОДЕЛАТЬ РЕАЛИЗАЦИЮ ВОЗВРАТА ТЕЛА ОШИБКИ
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto update(@PathVariable Long catId,
                              @Validated NewCategoryDto newCategoryDto) {
        return categoryService.update(catId, newCategoryDto);
        // ДОДЕЛАТЬ РЕАЛИЗАЦИЮ ВОЗВРАТА ТЕЛА ОШИБКИ
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        categoryService.delete(catId);
        // ДОДЕЛАТЬ РЕАЛИЗАЦИЮ ВОЗВРАТА ТЕЛА ОШИБКИ
    }
}
