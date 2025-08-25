package com.srivatsa.wedding.web;

import com.srivatsa.wedding.domain.Guest;
import com.srivatsa.wedding.domain.GuestDetails;
import com.srivatsa.wedding.service.RsvpService;
import com.srivatsa.wedding.web.dto.RsvpFormRequest;
import com.srivatsa.wedding.web.dto.RsvpFormResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rsvp")
public class RsvpController {

    private final RsvpService service;

    public RsvpController(RsvpService service) {
        this.service = service;
    }

    @GetMapping("/{inviteCode}")
    public RsvpFormResponse preload(@PathVariable String inviteCode) {
        var result = service.getForm(inviteCode);
        return RsvpFormResponse.builder()
                .guest(result.guest())
                .guestDetails(result.details())
                .rsvps(result.rsvps().stream().collect(Collectors.toList()))
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public RsvpFormResponse submit(@RequestBody RsvpFormRequest request) {
        Guest guestUpdates = Guest.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .lodgingName(request.getLodgingName())
                .build();
        GuestDetails detailsUpdates = GuestDetails.builder()
                .arrivalTs(request.getArrivalTs())
                .departureTs(request.getDepartureTs())
                .arrivalMethod(request.getArrivalMethod())
                .departureMethod(request.getDepartureMethod())
                .arrivalFlightNumber(request.getArrivalFlightNumber())
                .departureFlightNumber(request.getDepartureFlightNumber())
                .arrivalAirport(request.getArrivalAirport())
                .departureAirport(request.getDepartureAirport())
                .notes(request.getNotes())
                .build();
        var result = service.respondForMultiple(
                request.getEventStatuses() == null ? new Integer[]{} : request.getEventStatuses().keySet().toArray(new Integer[0]),
                request.getInviteCode(),
                request.getEventStatuses(),
                request.getDietary(),
                request.getComment(),
                guestUpdates,
                detailsUpdates);
        return RsvpFormResponse.builder()
                .guest(result.guest())
                .guestDetails(result.details())
                .rsvps(result.rsvps().stream().collect(Collectors.toList()))
                .build();
    }
}
