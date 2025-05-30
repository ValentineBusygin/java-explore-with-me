package ru.practicum.ewm.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.practicum.ewm.dto.location.LocationDto;
import ru.practicum.ewm.model.event.EventState;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EventFullDto extends EventShortDto {
    private String createdOn;
    private LocationDto location;
    private Long participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
}
