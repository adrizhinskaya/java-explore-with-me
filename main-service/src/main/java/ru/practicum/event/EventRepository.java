package ru.practicum.event;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.EventEntity;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

}
