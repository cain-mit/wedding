package com.srivatsa.wedding.web;

import com.srivatsa.wedding.domain.GuestDetails;
import com.srivatsa.wedding.service.RsvpService;
import com.srivatsa.wedding.web.dto.EventRsvpDto;
import com.srivatsa.wedding.web.dto.RsvpFormRequest;
import com.srivatsa.wedding.web.dto.RsvpFormResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.List;

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
        List<EventRsvpDto> eventDtos = result.events().stream()
                .map(event -> EventRsvpDto.builder()
                        .eventId(event.eventId())
                        .eventName(event.eventName())
                        .eventStartTs(event.eventStartTs())
                        .rsvpStatus(event.rsvpStatus())
                        .dietary(event.dietary())
                        .comment(event.comment())
                        .respondedAt(event.respondedAt())
                        .build())
                .collect(Collectors.toList());
        
        return RsvpFormResponse.builder()
                .guest(result.guest())
                .guestDetails(result.details())
                .events(eventDtos)
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public RsvpFormResponse submit(@RequestBody RsvpFormRequest request) {
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
                null, // no dietary updates
                null, // no comment updates
                null, // no guest updates
                detailsUpdates);
                
        List<EventRsvpDto> eventDtos = result.events().stream()
                .map(event -> EventRsvpDto.builder()
                        .eventId(event.eventId())
                        .eventName(event.eventName())
                        .eventStartTs(event.eventStartTs())
                        .rsvpStatus(event.rsvpStatus())
                        .dietary(event.dietary())
                        .comment(event.comment())
                        .respondedAt(event.respondedAt())
                        .build())
                .collect(Collectors.toList());
                
        return RsvpFormResponse.builder()
                .guest(result.guest())
                .guestDetails(result.details())
                .events(eventDtos)
                .build();
    }
}
