package ru.practicum.ewm.service.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Integer from, Integer size);

    public List<EventShortDto> getEventsByPublic(String text, List<Long> categories, Boolean paid,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                 Boolean onlyAvailable, String sort,
                                                 Integer from, Integer size, HttpServletRequest request);

    public EventFullDto updateEventAdmin(Long eventId, EventUpdateAdminRequest eventUpdateAdminRequest);

    public EventFullDto getEventById(Long eventId, HttpServletRequest request);

    public List<EventShortDto> getEventsCreatedByUser(Long userId, Integer from, Integer size);

    public EventFullDto addEvent(Long userId, EventNewDto eventNewDto);

    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    public EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, EventUpdateUserRequest eventUpdateUserRequest);

    public List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsForEvent(Long userId, Long eventId, @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateResult);
}
