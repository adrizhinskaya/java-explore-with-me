package ru.practicum.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.compilation.model.EventCompilationEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.compilation.model.EventsCompilationsId;
import ru.practicum.event.model.EventEntity;

import java.util.List;
import java.util.Optional;

public interface EventsCompilationsRepository extends JpaRepository<EventCompilationEntity, EventsCompilationsId> {
    List<EventCompilationEntity> findAllByCompilationId(Long compilationId);
    @Query("SELECT ec.event.id " +
            "FROM EventCompilationEntity ec " +
            "WHERE ec.compilation.id = :compilationId ")
    List<Long> findAllEventsByCompilationId(@Param("compilationId") Long compilationId);

}
