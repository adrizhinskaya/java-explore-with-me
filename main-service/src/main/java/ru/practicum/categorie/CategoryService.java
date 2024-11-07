package ru.practicum.categorie;

import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.categorie.model.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long catId);

    CategoryDto update(Long catId, NewCategoryDto newCategoryDto);

    void delete(Long catId);
}
