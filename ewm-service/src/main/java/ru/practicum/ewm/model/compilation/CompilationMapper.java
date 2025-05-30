package ru.practicum.ewm.model.compilation;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.CompilationNewDto;
import ru.practicum.ewm.model.event.EventMapper;

@UtilityClass
public class CompilationMapper {
    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream().map(EventMapper::toEventShortDto).toList())
                .build();
    }

    public Compilation toCompilation(CompilationNewDto compilationNewDto) {
        return Compilation.builder()
                .pinned(compilationNewDto.getPinned())
                .title(compilationNewDto.getTitle())
                .build();
    }
}
