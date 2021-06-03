package cz.cvut.kbss.ear.rest;

import cz.cvut.kbss.ear.exception.NotFoundException;
import cz.cvut.kbss.ear.exception.ValidationException;
import cz.cvut.kbss.ear.model.SportRoomReservation;
import cz.cvut.kbss.ear.model.User;
import cz.cvut.kbss.ear.service.RoomService;
import cz.cvut.kbss.ear.service.SportRoomReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/rest/reservations")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
public class SportRoomReservationController {

    private static final Logger LOG = LoggerFactory.getLogger(RoomController.class);
    private final RoomService roomService;
    private final SportRoomReservationService service;


    @Autowired
    public SportRoomReservationController(SportRoomReservationService service, RoomService roomService) {
        this.service = service;
        this.roomService = roomService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/delete_old_reservations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOldReservations() {
        service.removeReservationsOlderThan2Years();
        LOG.debug("Lectures older than 2 years were removed");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SportRoomReservation getById(@PathVariable Integer id) {
        final SportRoomReservation reservation = service.find(id);
        if (reservation == null) {
            throw NotFoundException.create("SportRoomReservation", id);
        }
        return reservation;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSportRoomReservation(@PathVariable Integer id) {
        final SportRoomReservation reservationToRemove = getById(id);
        if (reservationToRemove == null) {
            throw NotFoundException.create("SportRoomReservation", id);
        }
        roomService.removeSportRoomReservation(reservationToRemove);
        LOG.debug("RoomReservation {} removed from the schedule", reservationToRemove);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSportRoomReservation(@PathVariable Integer id, @RequestBody SportRoomReservation sportRoomReservation) {
        final SportRoomReservation original = getById(id);
        if (!original.getId().equals(sportRoomReservation.getId())) {
            throw new ValidationException("SportRoomReservation identifier in the data does not match the one in the request URL.");
        }
        service.update(sportRoomReservation);
        LOG.debug("Updated SportRoomReservation {}.", sportRoomReservation);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}/owner", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getReservationOwner(@PathVariable Integer id) {
        final SportRoomReservation reservation = service.find(id);
        if (reservation == null) {
            throw NotFoundException.create("SportRoomReservation", id);
        }
        return reservation.getOwner();
    }

}
