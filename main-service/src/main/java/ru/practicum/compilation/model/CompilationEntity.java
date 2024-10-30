package ru.practicum.compilation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "compilations", schema = "public")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "pinned")
    private Boolean pinned;
}
