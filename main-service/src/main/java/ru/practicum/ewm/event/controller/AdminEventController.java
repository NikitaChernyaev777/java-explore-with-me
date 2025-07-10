package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.event.dto.request.UpdateEventAdminRequestDto;
import ru.practicum.ewm.event.dto.response.EventFullResponseDto;
import ru.practicum.ewm.event.service.AdminEventService;

import java.util.List;

import static ru.practicum.ewm.helper.ControllerConstants.DEFAULT_TEN;
import static ru.practicum.ewm.helper.ControllerConstants.DEFAULT_ZERO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final AdminEventService adminEventService;

    @GetMapping
    public List<EventFullResponseDto> searchEventsByAdminFilters(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = DEFAULT_ZERO) @PositiveOrZero int from,
            @RequestParam(defaultValue = DEFAULT_TEN) @Positive int size) {

        log.info("GET /admin/events — фильтрация событий: users={}, states={}, categories={}, rangeStart={}, " +
                "rangeEnd={}, from={}, size={}", users, states, categories, rangeStart, rangeEnd, from, size);

        return adminEventService.searchEventsByAdminFilters(users, states, categories,
                rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullResponseDto updateAdminEvent(@PathVariable @Positive Long eventId,
                                                 @Valid @RequestBody UpdateEventAdminRequestDto updateEventAdminDto) {

        log.info("PATCH /admin/events/{} — обновление события администратором: {}", eventId, updateEventAdminDto);
        return adminEventService.updateAdminEvent(updateEventAdminDto, eventId);
    }
}