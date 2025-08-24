package com.srivatsa.wedding.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    private String phone;
    private String email;

    @Column(name = "invite_code", nullable = false, unique = true)
    private String inviteCode;

    @Column(name = "party_id")
    private Integer partyId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "lodging_name")
    private String lodgingName;

    @OneToOne(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private GuestDetails details;

    @Builder.Default
    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventInvitation> invitations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rsvp> rsvps = new ArrayList<>();
}
