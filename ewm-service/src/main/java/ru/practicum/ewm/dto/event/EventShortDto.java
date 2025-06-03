package ru.practicum.ewm.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EventShortDto {
    protected Long id;
    protected String annotation;
    protected String description;
    protected CategoryDto category;
    protected Long confirmedRequests;
    protected String eventDate;
    protected UserShortDto initiator;
    protected Boolean paid;
    protected String title;
    protected Long views;
    protected Long commentsCount;
}
