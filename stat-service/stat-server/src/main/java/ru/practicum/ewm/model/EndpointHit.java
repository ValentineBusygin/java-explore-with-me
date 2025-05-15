package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String app;

    private String uri;

    private String ip;

    @Column(name = "hit_time")
    private LocalDateTime timestamp;
}
