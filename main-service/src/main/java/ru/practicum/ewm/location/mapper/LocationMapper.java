package ru.practicum.ewm.location.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.model.Location;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {

    public static LocationDto toLocationDto(final Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}