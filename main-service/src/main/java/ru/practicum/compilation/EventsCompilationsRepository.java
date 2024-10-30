package ru.practicum.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.compilation.model.EventCompilationEntity;
import ru.practicum.compilation.model.EventCompilationId;

import java.util.List;

public interface EventsCompilationsRepository extends JpaRepository<EventCompilationEntity, EventCompilationId> {
    List<EventCompilationEntity> findAllByCompilationId(Long compilationId);

    @Query("SELECT ec.event.id " +
            "FROM EventCompilationEntity ec " +
            "WHERE ec.compilation.id = :compilationId ")
    List<Long> findAllEventsByCompilationId(@Param("compilationId") Long compilationId);

}
