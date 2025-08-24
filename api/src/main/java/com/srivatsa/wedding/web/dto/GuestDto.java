package com.srivatsa.wedding.web.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GuestDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String inviteCode;
    private Integer partyId;
    private OffsetDateTime createdAt;
    private String lodgingName;
}
