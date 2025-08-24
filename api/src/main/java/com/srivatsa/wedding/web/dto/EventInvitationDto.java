package com.srivatsa.wedding.web.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventInvitationDto {
    private Integer id;
    private Integer eventId;
    private Integer guestId;
    private Boolean invited;
}
