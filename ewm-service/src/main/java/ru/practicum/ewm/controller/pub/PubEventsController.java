package ru.practicum.ewm.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PubEventsController {
    private final EventService eventService;

    @GetMapping()
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
                                         @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(required = false, defaultValue = "10") @Positive Integer size,
                                         HttpServletRequest request) {

        return eventService.getEventsByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable("eventId") Long eventId,
                                     HttpServletRequest request) {
        return eventService.getEventById(eventId, request);
    }
}
