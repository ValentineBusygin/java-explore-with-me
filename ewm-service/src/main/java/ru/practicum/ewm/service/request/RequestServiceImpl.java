package ru.practicum.ewm.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UncorrectParameterException;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.location.LocationMapper;
import ru.practicum.ewm.model.request.Request;
import ru.practicum.ewm.model.request.RequestMapper;
import ru.practicum.ewm.model.request.RequestStatus;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.service.event.EventRepository;
import ru.practicum.ewm.service.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto add(Long userId, Long eventId) {
        User requester = getUser(userId);
        Event event = getEvent(eventId);

        validateRequest(event, requester);

        Request request = Request.builder()
                .requester(requester)
                .event(event)
                .created(LocalDateTime.now())
                .status(event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
                .build();

        request = requestRepository.save(request);

        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getAllByUserId(Long userId) {
        getUser(userId);

        List<Request> requests = requestRepository.findAllByRequesterId(userId);

        return requests.stream().map(RequestMapper::toParticipationRequestDto).toList();
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        getUser(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Заявка " + requestId + " не найдена"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException("Пользователь " + userId + " не является автором заявки " + requestId);
        }

        if (request.getStatus().equals(RequestStatus.CANCELED) || request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new UncorrectParameterException("Заявка " + requestId + " уже отменена");
        }

        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    private void validateRequest(Event event, User user) {
        if (event.getInitiator().getId().equals(user.getId())) {
            throw new ConflictException("Пользователь " + user.getId() + " не может подать заявку на участие в своем событии");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие " + event.getId() + " не опубликовано");
        }
        if (event.getParticipantLimit() != null && event.getParticipantLimit() > 0 &&
                    event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)) {
                throw new ConflictException("Достигнут лимит заявок на участие в событии " + event.getId());
        }
        if (requestRepository.existsByEventIdAndRequesterId(event.getId(), user.getId())) {
            throw new ConflictException("Пользователь " + user.getId() + " уже подал заявку на участие в событии " + event.getId());
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь " + userId + " не найден"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Событие " + eventId + " не найдено"));
    }
}
