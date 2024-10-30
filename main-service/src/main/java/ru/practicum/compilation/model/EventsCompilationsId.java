package ru.practicum.compilation.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EventsCompilationsId implements Serializable {
    private Long compilationId;
    private Long eventId;

    // Конструкторы, геттеры и сеттеры

    public EventsCompilationsId() {}

    public EventsCompilationsId(Long compilationId, Long eventId) {
        this.compilationId = compilationId;
        this.eventId = eventId;
    }

    public Long getCompilationId() {
        return compilationId;
    }

    public void setCompilationId(Long compilationId) {
        this.compilationId = compilationId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    // equals() и hashCode() для корректного сравнения объектов
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventsCompilationsId)) return false;
        EventsCompilationsId that = (EventsCompilationsId) o;
        return compilationId.equals(that.compilationId) && eventId.equals(that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compilationId, eventId);
    }
}
