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

        return ResponseEntity.ok(statsService.get(start, end, uris, unique));
    }
}
