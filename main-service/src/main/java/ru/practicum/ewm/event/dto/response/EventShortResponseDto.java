package ru.practicum.ewm.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.category.dto.response.CategoryResponseDto;
import ru.practicum.ewm.user.dto.response.UserShortResponseDto;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class EventShortResponseDto {
    private final Long id;
    private final String annotation;
    private final CategoryResponseDto category;
    private final Long confirmedRequests;
    private final String eventDate;
    private final UserShortResponseDto initiator;
    private final boolean paid;
    private final String title;
    private final Long views;
}