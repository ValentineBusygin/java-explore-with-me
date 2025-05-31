package ru.practicum.ewm.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompilationUpdateDto {
    private List<Long> events;
    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;
}
