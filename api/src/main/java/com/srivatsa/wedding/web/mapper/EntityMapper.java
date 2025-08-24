package com.srivatsa.wedding.web.mapper;

import com.srivatsa.wedding.domain.*;
import com.srivatsa.wedding.web.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    EventDto toDto(Event entity);
    Event toEntity(EventDto dto);

    GuestDto toDto(Guest entity);
    Guest toEntity(GuestDto dto);

    GuestDetailsDto toDto(GuestDetails entity);
    GuestDetails toEntity(GuestDetailsDto dto);

    EventInvitationDto toDto(EventInvitation entity);
    EventInvitation toEntity(EventInvitationDto dto);

    RsvpDto toDto(Rsvp entity);
    Rsvp toEntity(RsvpDto dto);

    @AfterMapping
    default void linkGuestDetails(@MappingTarget GuestDetails details, GuestDetailsDto dto) {
        if (details != null && details.getGuest() != null) {
            details.setGuestId(details.getGuest().getId());
        }
    }
}
