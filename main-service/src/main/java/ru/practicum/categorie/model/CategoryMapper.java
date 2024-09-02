package ru.practicum.categorie.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.categorie.model.dto.NewCategoryDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {
    public static CategoryEntity mapToCategoryEntity(NewCategoryDto newCategoryDto) {
        return CategoryEntity.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static CategoryDto mapToCategoryDto(CategoryEntity categoryEntity) {
        return CategoryDto.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .build();
    }

    public static List<CategoryDto> mapToCategoryDto(Iterable<CategoryEntity> categoryEntities) {
        List<CategoryDto> dtos = new ArrayList<>();
        for (CategoryEntity entity : categoryEntities) {
            dtos.add(mapToCategoryDto(entity));
        }
        return dtos;
    }
}
