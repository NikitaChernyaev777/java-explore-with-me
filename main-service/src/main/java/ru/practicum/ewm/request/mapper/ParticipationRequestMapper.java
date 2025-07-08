package ru.practicum.ewm.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.dto.response.ParticipationRequestResponseDto;
import ru.practicum.ewm.request.model.ParticipationRequest;

import static ru.practicum.ewm.constant.DateTimeFormatters.FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParticipationRequestMapper {

    public static ParticipationRequestResponseDto toParticipationRequestResponseDto(ParticipationRequest request) {
        return ParticipationRequestResponseDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .created(request.getCreated().format(FORMATTER))
                .build();
    }
}