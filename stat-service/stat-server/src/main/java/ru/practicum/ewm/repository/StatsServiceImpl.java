package ru.practicum.ewm.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.model.EndpointHitMapper;
import ru.practicum.ewm.model.Stats;
import ru.practicum.ewm.model.StatsMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public EndpointHitDto save(EndpointHitDto endpointHit) {
        return EndpointHitMapper.toEndpointHitDto(statsRepository.save(EndpointHitMapper.toEndpointHit(endpointHit)));
    }

    @Override
    public List<StatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты конца");
        }

        List<Stats> stats;
        if (unique.equals(Boolean.TRUE)) {
            stats = statsRepository.findUniqueStats(start, end, uris);
        } else {
            stats = statsRepository.findStats(start, end, uris);
        }

        return stats.stream()
                .map(StatsMapper::toStatsDto)
                .toList();
    }
}
