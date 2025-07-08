package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.request.NewCompilationRequestDto;
import ru.practicum.ewm.compilation.dto.request.UpdateCompilationRequestDto;
import ru.practicum.ewm.compilation.dto.response.CompilationResponseDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.helper.EntityHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EntityHelper entityHelper;

    @Override
    @Transactional
    public CompilationResponseDto createCompilation(NewCompilationRequestDto newCompilationDto) {
        log.info("Создание новой подборки событий с title={}", newCompilationDto.getTitle());

        Compilation compilation = CompilationMapper.toNewCompilation(newCompilationDto);
        compilation.setEvents(getEventsByIdsOrThrow(newCompilationDto.getEvents()));

        Compilation savedCompilation = compilationRepository.save(compilation);

        log.info("Подборка с id={}, title={} успешно создана", savedCompilation.getId(), savedCompilation.getTitle());
        return CompilationMapper.toCompilationResponseDto(savedCompilation);
    }

    @Override
    public List<CompilationResponseDto> getAllCompilations(Boolean pinned, int from, int size) {
        log.info("Получение списка подборки событий: from={}, size={}", from, size);

        Pageable pageRequest = entityHelper.toPageRequest(from, size);

        Page<Compilation> page = (pinned != null)
                ? compilationRepository.findAllByPinned(pinned, pageRequest)
                : compilationRepository.findAll(pageRequest);

        return page.stream()
                .map(CompilationMapper::toCompilationResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationResponseDto getCompilationById(Long compId) {
        log.info("Получение подборки событий с id={}", compId);

        Compilation compilation = getExistingCompilationByIdOrThrow(compId);

        log.info("Подборка событий с id={} найдена: {}", compId, compilation.getTitle());
        return CompilationMapper.toCompilationResponseDto(compilation);
    }

    @Override
    @Transactional
    public CompilationResponseDto updateCompilation(Long compId,
                                                    UpdateCompilationRequestDto updateCompilationDto) {
        log.info("Обновление подборки с id={} и title={}", compId, updateCompilationDto.getTitle());

        Compilation compilation = getExistingCompilationByIdOrThrow(compId);

        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }

        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }

        compilation.setEvents(getEventsByIdsOrThrow(updateCompilationDto.getEvents()));

        Compilation updatedCompilation = compilationRepository.save(compilation);

        log.info("Подборка с id={}, title={} успешно обновлена", updatedCompilation.getId(),
                updatedCompilation.getTitle());
        return CompilationMapper.toCompilationResponseDto(updatedCompilation);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compId) {
        log.info("Удаление подборки событий c id={}", compId);

        getExistingCompilationByIdOrThrow(compId);
        compilationRepository.deleteById(compId);

        log.info("Подборка событий с id={} успешно удалена", compId);
    }

    private Compilation getExistingCompilationByIdOrThrow(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Подборка событий с id=%d не найдено", compId)));
    }

    private Set<Event> getEventsByIdsOrThrow(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return new HashSet<>();
        }

        return eventIds.stream()
                .map(entityHelper::getExistingEventByIdOrThrow)
                .collect(Collectors.toSet());
    }
}