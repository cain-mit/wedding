package com.srivatsa.wedding.web;

import com.srivatsa.wedding.domain.Guest;
import com.srivatsa.wedding.domain.GuestDetails;
import com.srivatsa.wedding.service.GuestService;
import com.srivatsa.wedding.web.dto.GuestDetailsDto;
import com.srivatsa.wedding.web.dto.GuestDto;
import com.srivatsa.wedding.web.mapper.EntityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestService service;
    private final EntityMapper mapper;

    public GuestController(GuestService service, EntityMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<GuestDto> list() { return service.findAll().stream().map(mapper::toDto).toList(); }

    @GetMapping("/{id}")
    public GuestDto get(@PathVariable Integer id) { return mapper.toDto(service.findById(id)); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GuestDto create(@RequestBody GuestDto dto) { return mapper.toDto(service.create(mapper.toEntity(dto))); }

    @PutMapping("/{id}")
    public GuestDto update(@PathVariable Integer id, @RequestBody GuestDto dto) { return mapper.toDto(service.update(id, mapper.toEntity(dto))); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }

    @GetMapping("/{id}/details")
    public GuestDetailsDto getDetails(@PathVariable Integer id) {
        GuestDetails details = service.getDetails(id);
        return details == null ? null : mapper.toDto(details);
    }

    @PutMapping("/{id}/details")
    public GuestDetailsDto upsertDetails(@PathVariable Integer id, @RequestBody GuestDetailsDto dto) {
        GuestDetails entity = mapper.toEntity(dto);
        entity.setUpdatedAt(OffsetDateTime.now());
        return mapper.toDto(service.upsertDetails(id, entity));
    }
}
