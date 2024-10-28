package ru.practicum.categorie;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.categorie.model.CategoryEntity;
import ru.practicum.categorie.model.CategoryMapper;
import ru.practicum.categorie.model.dto.CategoryDto;
import ru.practicum.categorie.model.dto.NewCategoryDto;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.ConstraintConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.pagination.PaginationHelper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper mapper;

    private CategoryEntity categoryExistCheck(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFoundException("Category not found"));
    }

    private void nameAlreadyExistCheck(String newName, Long categoryId) {
        Optional<CategoryEntity> category = categoryRepository.findFirstByName(newName);
        if(category.isPresent() && !Objects.equals(category.get().getId(), categoryId)) {
            throw new ConstraintConflictException("Such categorie name already exsists .");
        }
    }

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        nameAlreadyExistCheck(newCategoryDto.getName(), 0L);
        CategoryEntity entity = categoryRepository.save(mapper.toCategoryEntity(newCategoryDto));
        return mapper.toCategoryDto(entity);
    }

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        List<CategoryEntity> entities = getAllWithPagination(from, size, null);
        return mapper.toCategoryDto(entities);
    }

    @Override
    public CategoryDto getById(Long catId) {
        return mapper.toCategoryDto(categoryExistCheck(catId));
    }

    @Override
    public CategoryDto update(Long catId, NewCategoryDto newCategoryDto) {
        CategoryEntity entity = categoryExistCheck(catId);
        nameAlreadyExistCheck(newCategoryDto.getName(), entity.getId());
        mapper.update(newCategoryDto, entity);
        CategoryEntity updatedEntity = categoryRepository.save(entity);
        return mapper.toCategoryDto(updatedEntity);
    }

    @Override
    public void delete(Long catId) {
        if(eventRepository.findFirstByCategoryId(catId).isPresent()) {
            throw new ConstraintConflictException("With this category associated some events .");
        }
        categoryRepository.deleteById(catId);
    }

    private List<CategoryEntity> getAllWithPagination(int from, int size, Sort sort) {
        PaginationHelper<CategoryEntity> ph = new PaginationHelper<>(from, size);
        Page<CategoryEntity> firstPage = categoryRepository.findAll(ph.getPageRequestForFirstPage(sort));
        Page<CategoryEntity> nextPage = firstPage.hasNext() ?
                categoryRepository.findAll(ph.getPageRequestForNextPage(sort)) : null;
        return ph.mergePages(firstPage, nextPage);
    }
}
