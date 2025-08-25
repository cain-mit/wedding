package com.srivatsa.wedding.web;

import com.srivatsa.wedding.domain.Guest;
import com.srivatsa.wedding.domain.GuestDetails;
import com.srivatsa.wedding.service.GuestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestService service;

    public GuestController(GuestService service) {
        this.service = service;
    }

    @GetMapping
    public List<Guest> list() { return service.findAll(); }

    @GetMapping("/{id}")
    public Guest get(@PathVariable Integer id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Guest create(@RequestBody Guest guest) { return service.create(guest); }

    @PutMapping("/{id}")
    public Guest update(@PathVariable Integer id, @RequestBody Guest guest) { return service.update(id, guest); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }

    @GetMapping("/{id}/details")
    public GuestDetails getDetails(@PathVariable Integer id) {
        return service.getDetails(id);
    }

    @PutMapping("/{id}/details")
    public GuestDetails upsertDetails(@PathVariable Integer id, @RequestBody GuestDetails details) {
        details.setUpdatedAt(OffsetDateTime.now());
        return service.upsertDetails(id, details);
    }
}
