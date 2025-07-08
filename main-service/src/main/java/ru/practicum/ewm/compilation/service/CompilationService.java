package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.request.NewCompilationRequestDto;
import ru.practicum.ewm.compilation.dto.request.UpdateCompilationRequestDto;
import ru.practicum.ewm.compilation.dto.response.CompilationResponseDto;

import java.util.List;

public interface CompilationService {

    CompilationResponseDto createCompilation(NewCompilationRequestDto newCompilationDto);

    List<CompilationResponseDto> getAllCompilations(Boolean pinned, int from, int size);

    CompilationResponseDto getCompilationById(Long compId);

    CompilationResponseDto updateCompilation(Long compId, UpdateCompilationRequestDto updateCompilationDto);

    void deleteCompilationById(Long compId);
}