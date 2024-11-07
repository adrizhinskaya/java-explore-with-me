package ru.practicum.categorie;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.categorie.model.dto.NewCategoryDto;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class CategoryMapper {
    public CategoryEntity toCategoryEntity(NewCategoryDto dto) {
        return CategoryEntity.builder()
                .name(dto.getName())
                .build();
    }

    public CategoryDto toCategoryDto(CategoryEntity entity) {
        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public List<CategoryDto> toCategoryDto(Iterable<CategoryEntity> entities) {
        List<CategoryDto> dtos = new ArrayList<>();
        for (CategoryEntity entity : entities) {
            dtos.add(toCategoryDto(entity));
        }
        return dtos;
    }

    public void update(NewCategoryDto dto, CategoryEntity entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
    }
}