package ru.practicum.ewm.compilation.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.compilation.dto.request.NewCompilationRequestDto;
import ru.practicum.ewm.compilation.dto.response.CompilationResponseDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.mapper.EventMapper;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {

    public static Compilation toNewCompilation(NewCompilationRequestDto newCompilationDto) {
        return Compilation.builder()
                .pinned(newCompilationDto.getPinned() != null ? newCompilationDto.getPinned() : false)
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static CompilationResponseDto toCompilationResponseDto(final Compilation compilation) {
        return CompilationResponseDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents() != null
                        ? compilation.getEvents().stream().map(EventMapper::toEventShortResponseDto).toList()
                        : new ArrayList<>())
                .build();
    }
}