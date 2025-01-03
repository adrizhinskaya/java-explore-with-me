package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.event.model.EventEntity;

import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long>, QuerydslPredicateExecutor<EventEntity> {
    Page<EventEntity> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<EventEntity> findByIdAndInitiatorId(Long id, Long initiatorId);

    Optional<EventEntity> findFirstByCategoryId(Long categoryId);
}
