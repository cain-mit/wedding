package com.srivatsa.wedding.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "guest_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GuestDetails {

    @Id
    @Column(name = "guest_id")
    private Integer guestId; // PK also FK

    @OneToOne
    @MapsId
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @Column(name = "arrival_ts")
    private OffsetDateTime arrivalTs;

    @Column(name = "departure_ts")
    private OffsetDateTime departureTs;

    @Column(name = "arrival_method")
    private String arrivalMethod; // consider enum mapping later

    @Column(name = "departure_method")
    private String departureMethod;

    @Column(name = "arrival_flight_number")
    private String arrivalFlightNumber;

    @Column(name = "departure_flight_number")
    private String departureFlightNumber;

    @Column(name = "arrival_airport")
    private String arrivalAirport;

    @Column(name = "departure_airport")
    private String departureAirport;

    private String notes;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
