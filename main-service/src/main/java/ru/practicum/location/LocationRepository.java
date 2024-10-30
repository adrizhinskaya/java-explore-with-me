package ru.practicum.location;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.location.model.LocationEntity;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    Optional<LocationEntity> findByLatAndLon(float lat, float lon);
}
