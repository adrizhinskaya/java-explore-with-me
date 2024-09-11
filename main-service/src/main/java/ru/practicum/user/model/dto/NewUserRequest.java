package ru.practicum.user.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewUserRequest {
    @NotBlank
    @Size(min = 2, message = "{validation.annotation.size.too_short}")
    @Size(max = 250, message = "{validation.annotation.size.too_long}")
    private String name;
    @NotBlank
    @Size(min = 6, message = "{validation.annotation.size.too_short}")
    @Size(max = 254, message = "{validation.annotation.size.too_long}")
    @Email
    private String email;
}
