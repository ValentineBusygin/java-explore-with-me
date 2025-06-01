package ru.practicum.ewm.service.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.location.LocationDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UncorrectParameterException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.comment.Comment;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.location.LocationMapper;
import ru.practicum.ewm.model.request.Request;
import ru.practicum.ewm.model.request.RequestMapper;
import ru.practicum.ewm.model.request.RequestStatus;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.service.category.CategoryRepository;
import ru.practicum.ewm.service.comment.CommentRepository;
import ru.practicum.ewm.service.location.LocationRepository;
import ru.practicum.ewm.service.request.RequestRepository;
import ru.practicum.ewm.service.user.UserRepository;
import ru.practicum.ewm.client.StatsClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    private final CommentRepository commentRepository;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${server.application.name:ewm-service}")
    private String applicationName;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        Specification<Event> spec = Specification.where(null);

        List<Long> userIds = users != null ? users : List.of();
        List<String> eventStates = states != null ? states : List.of();
        List<Long> categoryIds = categories != null ? categories : List.of();

        if (!userIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("initiator").get("id").in(userIds));
        }
        if (!eventStates.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("state").as(String.class).in(eventStates));
        }
        if (!categoryIds.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("category").get("id").in(categoryIds));
        }
        if (rangeStart != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        List<Event> events = eventRepository.findAll(spec, pageRequest).getContent();

        List<EventFullDto> eventsDto = events.stream()
                .map(EventMapper::toEventFullDto).toList();

        fillViewsCommentsAndConfirmedRequests(eventsDto, events);

        return eventsDto;
    }

    @Override
    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, EventUpdateAdminRequest eventUpdateAdminRequest) {
        Event event = getEvent(eventId);

        if (event.getState().equals(EventState.PUBLISHED) || event.getState().equals(EventState.CANCELED)) {
            throw new ConflictException("Cannot change published or canceled event");
        }

        boolean isUpdated = true;
        Event updatedEvent = updateEvent(event, eventUpdateAdminRequest);
        if (updatedEvent == null) {
            updatedEvent = event;
            isUpdated = false;
        }

        LocalDateTime eventDate = updatedEvent.getEventDate();
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException("Cannot publish event before 1 hours");
            }

            updatedEvent.setEventDate(eventDate);
            isUpdated = true;
        }

        EventStateAdmin stateAction = eventUpdateAdminRequest.getStateAction();
        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT -> {
                    updatedEvent.setState(EventState.PUBLISHED);
                    isUpdated = true;
                }
                case REJECT_EVENT -> {
                    updatedEvent.setState(EventState.CANCELED);
                    isUpdated = true;
                }
            };
        }

        Event savedEvent = null;
        if (isUpdated) {
            savedEvent = eventRepository.save(updatedEvent);
        }

        return savedEvent != null ? EventMapper.toEventFullDto(savedEvent) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new UncorrectParameterException(("rangeStart > rangeEnd"));
        }

        saveToStats(request);

        PageRequest page = PageRequest.of(from / size, size);
        Specification<Event> spec = Specification.where(null);
        LocalDateTime now = LocalDateTime.now();

        if (text != null) {
            String searchText = text.toLowerCase();
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("annotation")), "%" + searchText + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + searchText + "%")
                    ));
        }

        if (categories != null && !categories.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("category").get("id").in(categories));
        }

        LocalDateTime startDateTime = rangeStart != null ? rangeStart : now;
        spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("eventDate"), startDateTime));

        if (rangeEnd != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        if (onlyAvailable != null && onlyAvailable) {
            spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("state"), EventState.PUBLISHED));

        List<Event> events = eventRepository.findAll(spec, page).getContent();

        List<EventShortDto> eventsShortDto = events.stream().map(EventMapper::toEventShortDto).toList();

        fillViewsCommentsAndConfirmedRequests(eventsShortDto, events);

        return eventsShortDto;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = getEvent(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event " + eventId + " not published");
        }

        saveToStats(request);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);

        fillViewsCommentsAndConfirmedRequests(List.of(eventFullDto), List.of(event));

        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsCreatedByUser(Long userId, Integer from, Integer size) {
        getUser(userId);

        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("id").ascending());

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest).getContent();

        List<EventShortDto> eventsDto = events.stream()
                .map(EventMapper::toEventShortDto).toList();

        fillViewsCommentsAndConfirmedRequests(eventsDto, events);

        return eventsDto;
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, EventNewDto eventNewDto) {
        User user = getUser(userId);

        if (eventNewDto.getEventDate() != null && eventNewDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new UncorrectParameterException(("eventDate < now + 2 hours"));
        }

        Category category = getCategory(eventNewDto.getCategory());
        Event event = EventMapper.toEvent(eventNewDto);

        event.setCategory(category);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        if (eventNewDto.getLocation() != null) {
            Location location = locationRepository.save(LocationMapper.toLocation(eventNewDto.getLocation()));
            event.setLocation(location);
        }

        Event savedEvent = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(savedEvent);
        eventFullDto.setConfirmedRequests(0L);
        eventFullDto.setViews(0L);

        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        getUser(userId);
        Event event = getEvent(eventId, userId);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);

        fillViewsCommentsAndConfirmedRequests(List.of(eventFullDto), List.of(event));

        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, EventUpdateUserRequest eventUpdateUserRequest) {
        getUser(userId);

        Event event = getEvent(eventId, userId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(("Cannot update published event"));
        }

        boolean isUpdated = true;
        Event updatedEvent = updateEvent(event, eventUpdateUserRequest);
        if (updatedEvent == null) {
            updatedEvent = event;
            isUpdated = false;
        }

        LocalDateTime eventDate = eventUpdateUserRequest.getEventDate();
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException("Cannot update event before 1 hours");
            }

            updatedEvent.setEventDate(eventDate);
            isUpdated = true;
        }

        EventStateUser stateAction = eventUpdateUserRequest.getStateAction();
        if (stateAction != null) {
            switch (stateAction) {
                case SEND_TO_REVIEW -> {
                    updatedEvent.setState(EventState.PENDING);
                    isUpdated = true;
                }
                case CANCEL_REVIEW -> {
                    updatedEvent.setState(EventState.CANCELED);
                    isUpdated = true;
                }
            }
        }

        Event savedEvent = null;
        if (isUpdated) {
            savedEvent = eventRepository.save(updatedEvent);
        }
        return savedEvent != null ? EventMapper.toEventFullDto(savedEvent) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId) {
        getUser(userId);
        getEvent(eventId, userId);

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream().map(RequestMapper::toParticipationRequestDto).toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsForEvent(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateResult) {
        getUser(userId);
        Event event = getEvent(eventId);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Event does not require moderation");
        }

        RequestStatus status = eventRequestStatusUpdateResult.getStatus();
        int confirmedRequestsCount = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);

        if (confirmedRequestsCount >= event.getParticipantLimit()) {
            throw new ConflictException("Event already has maximum number of participants");
        }

        List<Long> requestIds = eventRequestStatusUpdateResult.getRequestIds().stream().toList();

        switch (status) {
            case CONFIRMED -> {
                List<Request> confirmedRequests = confirmRequestsForEvent(event,
                        requestIds, confirmedRequestsCount);

                List<Long> confirmedRequestsIds = confirmedRequests.stream().map(Request::getId).toList();

                List<Long> requestsForReject = requestIds.stream().filter(r -> !confirmedRequestsIds.contains(r)).toList();

                List<Request> rejectedRequests = rejectRequestsForEvent(event, requestsForReject);

                return EventRequestStatusUpdateResult.builder()
                        .confirmedRequests(confirmedRequests.stream().map(RequestMapper::toParticipationRequestDto).toList())
                        .rejectedRequests(rejectedRequests.stream().map(RequestMapper::toParticipationRequestDto).toList())
                        .build();

            }
            case REJECTED -> {
                List<Request> rejectedRequests = rejectRequestsForEvent(event, requestIds);

                return EventRequestStatusUpdateResult.builder()
                        .confirmedRequests(List.of())
                        .rejectedRequests(rejectedRequests.stream().map(RequestMapper::toParticipationRequestDto).toList())
                        .build();
            }
            default -> throw new UncorrectParameterException("Uncorrect status: " + status);
        }
    }

    private void saveToStats(HttpServletRequest request) {
        statsClient.save(EndpointHitDto.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(formatter))
                .build());
    }

    private List<Request> confirmRequestsForEvent(Event event, List<Long> requestIds, int confirmedRequestsCount) {
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> requests = getRequestsForEvent(event.getId(), requestIds);

        int availableRequestsCount = (int) (event.getParticipantLimit() - confirmedRequestsCount);

        for (Request request : requests) {
            if (availableRequestsCount <= 0) {
                break;
            }

            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                continue;
            }

            request.setStatus(RequestStatus.CONFIRMED);
            confirmedRequests.add(request);
            availableRequestsCount--;
        }

        requestRepository.saveAll(confirmedRequests);

        return confirmedRequests;
    }

    private List<Request> rejectRequestsForEvent(Event event, List<Long> requestIds) {
        List<Request> rejectedRequests = new ArrayList<>();
        List<Request> requests = getRequestsForEvent(event.getId(), requestIds);

        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                continue;
            }

            request.setStatus(RequestStatus.REJECTED);
            rejectedRequests.add(request);
        }

        requestRepository.saveAll(rejectedRequests);

        return rejectedRequests;
    }

    private List<Request> getRequestsForEvent(Long eventId, List<Long> requestIds) {
        return requestRepository.findByEventIdAndIdIn(eventId, requestIds).orElseThrow(
                () -> new NotFoundException("Requests with id = " + requestIds.toString() + " does not exist"
        ));
    }

    private Event getEvent(Long eventId) {
        return getEvent(eventId, null);
    }

    private Event getEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id = " + eventId + " does not exist"));
        if (userId != null && !event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event with id = " + eventId + " does not belong to user with id = " + userId);
        }

        return event;
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category with id = " + categoryId + " does not exist"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id = " + userId + " does not exist"));
    }

    private void fillViewsCommentsAndConfirmedRequests(List<? extends EventShortDto> eventsDto, List<Event> events) {
        List<Request> requests = requestRepository.findAllByEventIdInAndStatus(events.stream().map(Event::getId).toList(), RequestStatus.CONFIRMED);
        Map<Long, Long> confirmedRequestsMap = requests.stream().collect(Collectors.groupingBy(r -> r.getEvent().getId(), Collectors.counting()));

        for (EventShortDto eventDto : eventsDto) {
            Long count = confirmedRequestsMap.getOrDefault(eventDto.getId(), 0L);
            eventDto.setConfirmedRequests(count);
        }

        Map<Long, Long> viewsMap = getEventsViewsCount(events.stream().toList());
        for (EventShortDto eventDto : eventsDto) {
            Long count = viewsMap.getOrDefault(eventDto.getId(), 0L);
            eventDto.setViews(count);
        }

        Map<Long, Long> commentsCountMap = getEventsCommentsCount(events.stream().toList());
        for (EventShortDto eventDto : eventsDto) {
            Long count = commentsCountMap.getOrDefault(eventDto.getId(), 0L);
            eventDto.setCommentsCount(count);
        }
    }

    private Map<Long, Long> getEventsViewsCount(List<Event> events) {
        List<String> uris = events.stream().map(e -> "/events/" + e.getId()).toList();
        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        Map<Long, Long> viewStatsMap = null;
        if (start != null) {
            ResponseEntity<Object> responseEntity = statsClient.getStats(start.format(formatter), LocalDateTime.now().format(formatter), uris, true);
            List<StatsDto> stats = objectMapper.convertValue(responseEntity.getBody(), new TypeReference<>() {});

            viewStatsMap = stats.stream()
                    .filter(statsDto -> statsDto.getUri().startsWith("/events/"))
                    .collect(Collectors.toMap(statsDto -> Long.parseLong(statsDto.getUri().split("/")[2]), StatsDto::getHits));
        }

        return viewStatsMap;
    }

    private Map<Long, Long> getEventsCommentsCount(List<Event> events) {
        List<Comment> comments = commentRepository.findAllByEventIdIn(events.stream().map(Event::getId).toList());

        return comments.stream()
                .collect(Collectors.toMap(comment -> comment.getEvent().getId(), comment -> 1L, Long::sum));
    }

    private Event updateEvent(Event oldEvent, EventUpdateRequest eventUpdateRequest) {
        boolean isUpdated = false;

        String annotation = eventUpdateRequest.getAnnotation();
        if (annotation != null && !annotation.isBlank() && !annotation.equals(oldEvent.getAnnotation())) {
            oldEvent.setAnnotation(annotation);
            isUpdated = true;
        }

        Long categoryId = eventUpdateRequest.getCategory();
        if (categoryId != null) {
            Category category = getCategory(categoryId);
            oldEvent.setCategory(category);
            isUpdated = true;
        }

        String description = eventUpdateRequest.getDescription();
        if (description != null && !description.isBlank() && !description.equals(oldEvent.getDescription())) {
            oldEvent.setDescription(description);
            isUpdated = true;
        }

        LocationDto locationDto = eventUpdateRequest.getLocation();
        if (locationDto != null) {
            Location location = LocationMapper.toLocation(locationDto);

            locationRepository.save(location);

            oldEvent.setLocation(location);
            isUpdated = true;
        }

        Long participantLimit = eventUpdateRequest.getParticipantLimit();
        if (participantLimit != null && !participantLimit.equals(oldEvent.getParticipantLimit())) {
            oldEvent.setParticipantLimit(participantLimit);
            isUpdated = true;
        }

        Boolean paid = eventUpdateRequest.getPaid();
        if (paid != null && !paid.equals(oldEvent.getPaid())) {
            oldEvent.setPaid(paid);
            isUpdated = true;
        }

        Boolean requestModeration = eventUpdateRequest.getRequestModeration();
        if (requestModeration != null && !requestModeration.equals(oldEvent.getRequestModeration())) {
            oldEvent.setRequestModeration(requestModeration);
            isUpdated = true;
        }

        String title = eventUpdateRequest.getTitle();
        if (title != null && !title.isBlank() && !title.equals(oldEvent.getTitle())) {
            oldEvent.setTitle(title);
            isUpdated = true;
        }

        return isUpdated ? oldEvent : null;
    }
}
