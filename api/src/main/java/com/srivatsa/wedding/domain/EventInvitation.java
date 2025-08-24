package com.srivatsa.wedding.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_invitations", uniqueConstraints = {
        @UniqueConstraint(name = "event_invitations_event_id_guest_id_key", columnNames = {"event_id", "guest_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    private Guest guest;

    private Boolean invited;
}
