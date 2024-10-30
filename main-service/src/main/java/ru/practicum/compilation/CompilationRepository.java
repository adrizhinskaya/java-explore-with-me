package ru.practicum.compilation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.compilation.model.CompilationEntity;

import java.util.Optional;

public interface CompilationRepository extends JpaRepository<CompilationEntity, Long> {
    Optional<CompilationEntity> findFirstByTitle(String title);

    Page<CompilationEntity> findAllByPinned(Boolean pinned, Pageable pageable);
}
