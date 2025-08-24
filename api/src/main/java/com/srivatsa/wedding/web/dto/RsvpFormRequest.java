package com.srivatsa.wedding.web.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RsvpFormRequest {
    private String inviteCode; // required
    private Map<Integer, String> eventStatuses; // eventId -> yes/no/maybe
    private String dietary; // applied to all
    private String comment; // applied to all

    // guest updates
    private String name;
    private String phone;
    private String email;
    private String lodgingName;

    // guest details updates
    private OffsetDateTime arrivalTs;
    private OffsetDateTime departureTs;
    private String arrivalMethod;
    private String departureMethod;
    private String arrivalFlightNumber;
    private String departureFlightNumber;
    private String arrivalAirport;
    private String departureAirport;
    private String notes;
}
