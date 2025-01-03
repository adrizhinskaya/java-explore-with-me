package ru.practicum.user.model.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(of = {"id"})
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
