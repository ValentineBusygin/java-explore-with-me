package ru.practicum.ewm.model;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.utils.SimpleDateTimeFormatter;

@UtilityClass
public class EndpointHitMapper {
    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(SimpleDateTimeFormatter.fromString(endpointHitDto.getTimestamp()))
                .build();
    }

    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(SimpleDateTimeFormatter.toString(endpointHit.getTimestamp()))
                .build();
    }
}
