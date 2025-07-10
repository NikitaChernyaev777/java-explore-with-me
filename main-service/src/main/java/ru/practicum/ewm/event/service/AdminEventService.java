package ru.practicum.ewm.event.service;

import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.event.dto.request.UpdateEventAdminRequestDto;
import ru.practicum.ewm.event.dto.response.EventFullResponseDto;

import java.util.List;

public interface AdminEventService {

    List<EventFullResponseDto> searchEventsByAdminFilters(List<Long> users, List<EventState> states,
                                                          List<Long> categories, String rangeStart, String rangeEnd,
                                                          int from, int size);

    EventFullResponseDto updateAdminEvent(UpdateEventAdminRequestDto updateEventAdminDto, Long eventId);
}