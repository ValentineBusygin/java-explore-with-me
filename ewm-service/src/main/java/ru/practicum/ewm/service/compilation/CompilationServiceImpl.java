package ru.practicum.ewm.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.CompilationNewDto;
import ru.practicum.ewm.dto.compilation.CompilationUpdateDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.compilation.Compilation;
import ru.practicum.ewm.model.compilation.CompilationMapper;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.service.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public void delete(Long compId) {
        getCompilationById(compId);

        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto create(CompilationNewDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);

        if (compilation.getPinned() == null) {
            compilation.setPinned(Boolean.FALSE);
        }

        if (newCompilationDto.getEvents() != null) {
            List<Long> eventsIds = newCompilationDto.getEvents();
            List<Event> events = eventRepository.findAllById(eventsIds);
            compilation.setEvents(events);
        } else {
            compilation.setEvents(List.of());
        }

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto update(Long compId, CompilationUpdateDto compilationUpdateDto) {
        Compilation compilation = getCompilationById(compId);
        List<Long> eventsIds = compilationUpdateDto.getEvents();
        if (eventsIds != null) {
            List<Event> events = eventRepository.findAllById(eventsIds);
            compilation.setEvents(events);
        }

        if (compilationUpdateDto.getPinned() != null) {
            compilation.setPinned(compilationUpdateDto.getPinned());
        }

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        }

        return compilations.stream().map(CompilationMapper::toCompilationDto).toList();
    }

    @Override
    public CompilationDto getById(Long compId) {
        return CompilationMapper.toCompilationDto(getCompilationById(compId));
    }

    private Compilation getCompilationById(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation " + compId + "not found"));
    }
}
