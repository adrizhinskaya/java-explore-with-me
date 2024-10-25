//package ru.practicum.compilation.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//import ru.practicum.event.model.dto.EventShortDto;
//
//import java.util.List;
//
//@Entity
//@Table(name = "compilations", schema = "public")
//@Builder
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class CompilationEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private List<EventShortDto> events;//////////////////???????????????????????????
//    @Column(name = "pinned", nullable = false)
//    private Boolean pinned;
//    @Column(name = "title", nullable = false)
//    private String title;
//}
