package ru.practicum.ewm.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Stats {
    private String app;
    private String uri;
    private Integer hits;
}
