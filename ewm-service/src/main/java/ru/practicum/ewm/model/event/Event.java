package ru.practicum.ewm.model.event;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    private Boolean paid;

    @Column(name = "participant_limit")
    private Long participantLimit;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private EventState state;

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
