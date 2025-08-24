package com.srivatsa.wedding.web.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RsvpFormResponse {
    private GuestDto guest;
    private GuestDetailsDto guestDetails;
    private List<RsvpDto> rsvps;
}
