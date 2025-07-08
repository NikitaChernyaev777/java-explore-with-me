package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.request.NewEventRequestDto;
import ru.practicum.ewm.event.dto.request.UpdateEventUserRequestDto;
import ru.practicum.ewm.event.dto.response.EventFullResponseDto;
import ru.practicum.ewm.event.dto.response.EventShortResponseDto;
import ru.practicum.ewm.event.service.PrivateEventService;
import ru.practicum.ewm.request.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.request.dto.response.ParticipationRequestResponseDto;
import ru.practicum.ewm.request.dto.response.EventRequestStatusUpdateResultResponseDto;

import java.util.List;

import static ru.practicum.ewm.helper.ControllerConstants.DEFAULT_TEN;
import static ru.practicum.ewm.helper.ControllerConstants.DEFAULT_ZERO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class PrivateEventController {

    private final PrivateEventService privateEventService;

    @GetMapping("/{userId}/events")
    public List<EventShortResponseDto> getUserEvents(
            @PathVariable @Positive Long userId,
            @RequestParam(name = "from", defaultValue = DEFAULT_ZERO) @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = DEFAULT_TEN) @Positive int size) {

        log.info("GET /users/{}/events?from={}&size={} — получение списка событий пользователя", userId, from, size);
        return privateEventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullResponseDto getUserEventById(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long eventId) {

        log.info("GET /users/{}/events/{} — получение события пользователем", userId, eventId);
        return privateEventService.getUserEventById(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestResponseDto> getParticipationRequests(@PathVariable @Positive Long userId,
                                                                          @PathVariable @Positive Long eventId) {

        log.info("GET /users/{}/events/{}/requests — получение заявок на участие", userId, eventId);
        return privateEventService.getParticipationRequests(userId, eventId);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullResponseDto createEvent(@Valid @RequestBody NewEventRequestDto newEventDto,
                                            @PathVariable @Positive Long userId) {

        log.info("POST /users/{}/events — создание события: {}", userId, newEventDto);
        return privateEventService.createEvent(newEventDto, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullResponseDto updateUserEvent(@Valid @RequestBody UpdateEventUserRequestDto updateEventUserDto,
                                                @PathVariable @Positive Long userId,
                                                @PathVariable @Positive Long eventId) {

        log.info("PATCH /users/{}/events/{} — обновление события пользователем: {}", userId, eventId,
                updateEventUserDto);
        return privateEventService.updateUserEvent(updateEventUserDto, userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResultResponseDto updateParticipationRequestStatuses(
            @Valid @RequestBody EventRequestStatusUpdateRequestDto updateResult,
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId) {

        log.info("PATCH /users/{}/events/{}/requests — обновление статусов заявок: {}", userId, eventId, updateResult);
        return privateEventService.updateParticipationRequestStatuses(updateResult, userId, eventId);
    }
}