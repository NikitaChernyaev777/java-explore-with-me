package ru.practicum.ewm.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.compilation.dto.response.CompilationResponseDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import java.util.List;

import static ru.practicum.ewm.helper.ControllerConstants.DEFAULT_TEN;
import static ru.practicum.ewm.helper.ControllerConstants.DEFAULT_ZERO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationResponseDto> getAllCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = DEFAULT_ZERO) @PositiveOrZero int from,
            @RequestParam(defaultValue = DEFAULT_TEN) @Positive int size) {

        log.info("GET /compilations — фильтрация подборок: pinned={}, from={}, size={}", pinned, from, size);
        return compilationService.getAllCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationResponseDto getCompilationById(@PathVariable @Positive Long compId) {
        log.info("GET /compilations/{} — получение подборки", compId);
        return compilationService.getCompilationById(compId);
    }
}