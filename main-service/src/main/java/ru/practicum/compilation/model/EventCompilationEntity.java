package ru.practicum.compilation.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.event.model.EventEntity;

@Entity
@Table(name = "events_compilations")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventCompilationEntity {
    @EmbeddedId
    private EventCompilationId id;
    @ManyToOne
    @MapsId("compilationId")
    @JoinColumn(name = "compilation_id")
    private CompilationEntity compilation;
    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private EventEntity event;
}
