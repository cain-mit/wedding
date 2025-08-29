package com.srivatsa.wedding.web.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventRsvpDto {
    private Integer eventId;
    private String eventName;
    private OffsetDateTime eventStartTs;
    private String rsvpStatus; // null if no RSVP exists
    private String dietary;
    private String comment;
    private OffsetDateTime respondedAt;
}
