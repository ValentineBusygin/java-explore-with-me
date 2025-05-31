package ru.practicum.ewm.service.request;

import jakarta.validation.constraints.Min;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto add(@Min(0) Long userId, @Min(0) Long eventId);

    List<ParticipationRequestDto> getAllByUserId(@Min(0) Long userId);

    ParticipationRequestDto cancelRequest(@Min(0) Long userId, @Min(0) Long requestId);
}
