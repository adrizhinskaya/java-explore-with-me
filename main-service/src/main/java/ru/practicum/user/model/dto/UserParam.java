package ru.practicum.user.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
public class UserParam {
    private Set<Long> ids;
    private Integer from;
    private Integer size;
}
