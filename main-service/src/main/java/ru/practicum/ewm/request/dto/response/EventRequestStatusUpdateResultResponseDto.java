package ru.practicum.ewm.request.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class EventRequestStatusUpdateResultResponseDto {
    private final List<ParticipationRequestResponseDto> confirmedRequests;
    private final List<ParticipationRequestResponseDto> rejectedRequests;
}