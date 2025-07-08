package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.response.ParticipationRequestResponseDto;

import java.util.List;

public interface ParticipationRequestService {

    ParticipationRequestResponseDto createParticipationRequest(Long userId, Long eventId);

    List<ParticipationRequestResponseDto> getUserParticipationRequests(Long userId);

    ParticipationRequestResponseDto cancelParticipationRequest(Long userId, Long requestId);
}