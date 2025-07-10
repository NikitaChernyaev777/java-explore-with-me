package ru.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.compilation.dto.request.NewCompilationRequestDto;
import ru.practicum.ewm.compilation.dto.request.UpdateCompilationRequestDto;
import ru.practicum.ewm.compilation.dto.response.CompilationResponseDto;
import ru.practicum.ewm.compilation.service.CompilationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto createCompilation(@Valid @RequestBody NewCompilationRequestDto newCompilationDto) {
        log.info("POST /admin/compilations — создание подборки администратором с title={}",
                newCompilationDto.getTitle());
        return compilationService.createCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationResponseDto updateCompilation(
            @PathVariable @Positive Long compId,
            @Valid @RequestBody UpdateCompilationRequestDto updateCompilationDto) {

        log.info("PATCH /admin/compilations/{} — обновление подборки: title={}, pinned={}, events={}", compId,
                updateCompilationDto.getTitle(), updateCompilationDto.getPinned(), updateCompilationDto.getEvents());
        return compilationService.updateCompilation(compId, updateCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@PathVariable @Positive Long compId) {
        log.info("DELETE /admin/compilations/{} — удаление подборки администратором", compId);
        compilationService.deleteCompilationById(compId);
    }
}