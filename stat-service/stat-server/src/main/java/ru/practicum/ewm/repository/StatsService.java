package ru.practicum.ewm.repository;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    public EndpointHitDto save(EndpointHitDto endpointHit);

    public List<StatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
