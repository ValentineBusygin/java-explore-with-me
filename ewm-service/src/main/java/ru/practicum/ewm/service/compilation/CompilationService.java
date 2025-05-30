package ru.practicum.ewm.service.compilation;

import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.CompilationNewDto;
import ru.practicum.ewm.dto.compilation.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {
    void delete(Long compId);

    CompilationDto create(CompilationNewDto newCompilationDto);

    CompilationDto update(Long compId, CompilationUpdateDto compilationUpdateDto);

    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto getById(Long compId);
}
