package com.srivatsa.wedding.web.dto;

import com.srivatsa.wedding.domain.Guest;
import com.srivatsa.wedding.domain.GuestDetails;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RsvpFormResponse {
    private Guest guest;
    private GuestDetails guestDetails;
    private List<EventRsvpDto> events;
}
