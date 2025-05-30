package ru.practicum.ewm.model.event;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventNewDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.model.category.CategoryMapper;
import ru.practicum.ewm.model.location.LocationMapper;
import ru.practicum.ewm.model.user.UserMapper;

@UtilityClass
public class EventMapper {
    public Event toEvent(EventNewDto eventNewDto) {
        return Event.builder()
                .id(null)
                .annotation(eventNewDto.getAnnotation())
                .description(eventNewDto.getDescription())
                .eventDate(eventNewDto.getEventDate())
                .location(eventNewDto.getLocation())
                .paid(eventNewDto.getPaid())
                .title(eventNewDto.getTitle())
                .participantLimit(eventNewDto.getParticipantLimit())
                .requestModeration(eventNewDto.getRequestModeration())
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn().format(Event.formatter))
                .eventDate(event.getEventDate().format(Event.formatter))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate().format(Event.formatter))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }
}
