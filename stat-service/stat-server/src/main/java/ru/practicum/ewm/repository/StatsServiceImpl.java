package ru.practicum.ewm.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.model.EndpointHitMapper;
import ru.practicum.ewm.model.StatsMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
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

        if (unique.equals(Boolean.TRUE)) {
            return statsRepository.findUniqueStats(start, end, uris)
                    .stream()
                    .map(StatsMapper::toStatsDto)
                    .toList();
        } else {
            return statsRepository.findStats(start, end, uris)
                    .stream()
                    .map(StatsMapper::toStatsDto)
                    .toList();
        }
    }
}
