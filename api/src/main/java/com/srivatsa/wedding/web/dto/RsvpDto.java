package com.srivatsa.wedding.web.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RsvpDto {
    private Integer id;
    private Integer eventId;
    private Integer guestId;
    private String status;
    private String dietary;
    private String comment;
    private OffsetDateTime respondedAt;
}
