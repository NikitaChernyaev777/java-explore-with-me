package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.response.EventFullResponseDto;
import ru.practicum.ewm.event.dto.response.EventShortResponseDto;

import java.util.List;

public interface PublicEventService {

    List<EventShortResponseDto> searchPublicEvents(String text, List<Long> categories, Boolean paid,
                                                   String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                   String sort, int from, int size);

    EventFullResponseDto getEventById(Long eventId);
}