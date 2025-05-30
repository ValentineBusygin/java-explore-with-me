package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.model.location.Location;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventNewDto {

    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @NotNull
    @Positive
    private Long category;

    private Long confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    @Valid
    private Location location;

    private Boolean paid = false;

    @PositiveOrZero
    private Long participantLimit = 0L;

    private Boolean requestModeration = true;

    @NotNull
    @Length(min = 3, max = 120)
    private String title;
}
