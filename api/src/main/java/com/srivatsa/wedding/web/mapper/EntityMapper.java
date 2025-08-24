package com.srivatsa.wedding.web.mapper;

import com.srivatsa.wedding.domain.*;
import com.srivatsa.wedding.web.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EntityMapper {
    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "rsvps", ignore = true)
    Event toEntity(EventDto dto);
    EventDto toDto(Event entity);

    @Mapping(target = "details", ignore = true)
    @Mapping(target = "invitations", ignore = true)
    @Mapping(target = "rsvps", ignore = true)
    Guest toEntity(GuestDto dto);
    GuestDto toDto(Guest entity);

    @Mapping(target = "guest", ignore = true)
    GuestDetails toEntity(GuestDetailsDto dto);
    GuestDetailsDto toDto(GuestDetails entity);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "guestId", source = "guest.id")
    EventInvitationDto toDto(EventInvitation entity);
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "guest", ignore = true)
    EventInvitation toEntity(EventInvitationDto dto);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "guestId", source = "guest.id")
    RsvpDto toDto(Rsvp entity);
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "guest", ignore = true)
    Rsvp toEntity(RsvpDto dto);
}
