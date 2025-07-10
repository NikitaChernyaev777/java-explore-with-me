package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.request.NewEventRequestDto;
import ru.practicum.ewm.event.dto.request.UpdateEventUserRequestDto;
import ru.practicum.ewm.event.dto.response.EventFullResponseDto;
import ru.practicum.ewm.event.dto.response.EventShortResponseDto;
import ru.practicum.ewm.request.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.request.dto.response.ParticipationRequestResponseDto;
import ru.practicum.ewm.request.dto.response.EventRequestStatusUpdateResultResponseDto;

import java.util.List;

public interface PrivateEventService {

    EventFullResponseDto createEvent(NewEventRequestDto newEventDto, Long userId);

    List<EventShortResponseDto> getUserEvents(Long userId, int from, int size);

    EventFullResponseDto getUserEventById(Long userId, Long eventId);

    EventFullResponseDto updateUserEvent(UpdateEventUserRequestDto updateEventUserDto, Long userId, Long eventId);

    List<ParticipationRequestResponseDto> getParticipationRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResultResponseDto updateParticipationRequestStatuses(
            EventRequestStatusUpdateRequestDto updateResult, Long userId, Long eventId);
}