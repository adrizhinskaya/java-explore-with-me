package ru.practicum.comment.model.param;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
public class AdminCommentParam {
    private String text;
    private Set<Long> comments;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean sort;
    private int from;
    private int size;
}