package ru.practicum.ewm.controller.priv;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventNewDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.EventUpdateUserRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class PrivateEventsController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsByUserId(@PathVariable(value = "userId") Long userId,
                                                 @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        return eventService.getEventsCreatedByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable(value = "userId") Long userId,
                                 @RequestBody @Valid EventNewDto eventNewDto) {
        return eventService.addEvent(userId, eventNewDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUserId(@PathVariable(value = "userId") Long userId,
                                         @PathVariable(value = "eventId") Long eventId) {
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUserId(@PathVariable(value = "userId") Long userId,
                                            @PathVariable(value = "eventId") Long eventId,
                                            @RequestBody @Valid EventUpdateUserRequest eventUpdateUserRequest) {
        return eventService.updateEventByUserIdAndEventId(userId, eventId, eventUpdateUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsForEventByUserId(@PathVariable(value = "userId") Long userId,
                                                                     @PathVariable(value = "eventId") Long eventId) {
        return eventService.getRequestsForEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsForEventByUserId(@PathVariable(value = "userId") Long userId,
                                                                         @PathVariable(value = "eventId") Long eventId,
                                                                         @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return eventService.updateRequestsForEvent(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
