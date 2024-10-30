package ru.practicum.compilation.model;

import jakarta.persistence.*;
import lombok.Builder;
import ru.practicum.compilation.model.CompilationEntity;
import ru.practicum.compilation.model.EventsCompilationsId;
import ru.practicum.event.model.EventEntity;


@Entity
@Table(name = "events_compilations")
@Builder
public class EventCompilationEntity {

    @EmbeddedId
    private EventsCompilationsId id;

    @ManyToOne
    @MapsId("compilationId")
    @JoinColumn(name = "compilation_id")
    private CompilationEntity compilation;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private EventEntity event;

    // Конструкторы, геттеры и сеттеры

    public EventCompilationEntity() {}

    public EventCompilationEntity(EventsCompilationsId id, CompilationEntity compilation, EventEntity event) {
        this.id = id;
        this.compilation = compilation;
        this.event = event;
    }

    public EventsCompilationsId getId() {
        return id;
    }

    public void setId(EventsCompilationsId id) {
        this.id = id;
    }

    public CompilationEntity getCompilation() {
        return compilation;
    }

    public void setCompilation(CompilationEntity compilation) {
        this.compilation = compilation;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }
}
