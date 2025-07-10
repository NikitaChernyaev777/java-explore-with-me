package ru.practicum.ewm.request.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.enums.RequestStatus;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ParticipationRequestResponseDto {
    private final Long id;
    private final String created;
    private final Long event;
    private final Long requester;
    private final RequestStatus status;
}