package ru.practicum.location;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {
    public static LocationEntity mapToLocationEntity(Location location) {
        return LocationEntity.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static Location mapToLocation(LocationEntity locationEntity) {
        return Location.builder()
                .lat(locationEntity.getLat())
                .lon(locationEntity.getLon())
                .build();
    }
}
