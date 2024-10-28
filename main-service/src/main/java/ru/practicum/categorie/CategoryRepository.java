package ru.practicum.categorie;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.categorie.model.CategoryEntity;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findFirstByName(String name);
}
