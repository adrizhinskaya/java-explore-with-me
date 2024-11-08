package ru.practicum.comment.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class CommentDto {
    @NotBlank(message = "Comment content must not be blank")
    @Size(min = 5, message = "Comment content must be longer than 5 symbols")
    @Size(max = 500, message = "Comment content must be shorter than 500 symbols")
    private String content;
}