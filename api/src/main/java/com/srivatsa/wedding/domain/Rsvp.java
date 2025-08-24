package com.srivatsa.wedding.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "rsvps", uniqueConstraints = {
        @UniqueConstraint(name = "rsvps_event_id_guest_id_key", columnNames = {"event_id", "guest_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rsvp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @Column(nullable = false)
    private String status; // yes/no/maybe (could be enum)

    private String dietary;

    private String comment;

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;
}
