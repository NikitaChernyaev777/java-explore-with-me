package ru.practicum.ewm.compilation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.event.dto.response.EventShortResponseDto;

import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CompilationResponseDto {
    private final Long id;
    private final List<EventShortResponseDto> events;
    private final Boolean pinned;
    private final String title;
}