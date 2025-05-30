package ru.practicum.ewm.model.location;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.location.LocationDto;

@UtilityClass
public class LocationMapper {
    public LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();

    }

    public Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }
}
