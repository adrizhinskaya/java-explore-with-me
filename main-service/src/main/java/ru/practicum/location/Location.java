package ru.practicum.location;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Location {
    private float lat;
    private float lon;
}
