package ru.practicum.ewm.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.category.dto.response.CategoryResponseDto;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.user.dto.response.UserShortResponseDto;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class EventFullResponseDto {

    private final Long id;
    private final String annotation;
    private final CategoryResponseDto category;
    private final Long confirmedRequests;

    private final String createdOn;
    private final String description;
    private final String eventDate;

    private final UserShortResponseDto initiator;
    private final LocationDto location;

    private final boolean paid;
    private final Integer participantLimit;
    private final String publishedOn;
    private final boolean requestModeration;

    private final EventState state;
    private final String title;
    private final Long views;
}