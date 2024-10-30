package ru.practicum.location;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class LocationMapper {
    public LocationEntity toLocationEntity(Location location) {
        return LocationEntity.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public Location toLocation(LocationEntity entity) {
        return Location.builder()
                .lat(entity.getLat())
                .lon(entity.getLon())
                .build();
    }
}

