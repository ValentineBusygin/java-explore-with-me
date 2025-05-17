package ru.practicum.ewm.model;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.StatsDto;

@UtilityClass
public class StatsMapper {
    public Stats toStats(StatsDto statsDto) {
        return Stats.builder()
                .app(statsDto.getApp())
                .uri(statsDto.getUri())
                .hits(statsDto.getHits())
                .build();
    }

    public StatsDto toStatsDto(Stats stats) {
        return StatsDto.builder()
                .app(stats.getApp())
                .uri(stats.getUri())
                .hits(stats.getHits())
                .build();
    }
}
