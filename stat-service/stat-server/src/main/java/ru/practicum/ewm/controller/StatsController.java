package ru.practicum.ewm.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.repository.StatsService;
import ru.practicum.ewm.utils.SimpleDateTimeFormatter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@AllArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> addHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Получен запрос на запись статистики: {}", endpointHitDto);

        return new ResponseEntity<>(statsService.save(endpointHitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam @DateTimeFormat(pattern = SimpleDateTimeFormatter.DATE_TIME_FORMAT) LocalDateTime start,
                                           @RequestParam @DateTimeFormat(pattern = SimpleDateTimeFormatter.DATE_TIME_FORMAT) LocalDateTime end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получен запрос на получение статистики");

        List<String> parsedUris = new ArrayList<>();
        for (String uri : uris) {
            if (uri.charAt(0) == '[') {
                parsedUris.addAll(List.of(uri.substring(1, uri.length() - 1).split(",")));
            } else {
                parsedUris.add(uri);
            }
        }

        return ResponseEntity.ok(statsService.get(start, end, parsedUris, unique));
    }
}
