package cz.cvut.kbss.ear.rest;


import cz.cvut.kbss.ear.exception.NotFoundException;
import cz.cvut.kbss.ear.exception.ValidationException;
import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.SportRoomReservation;
import cz.cvut.kbss.ear.rest.util.RestUtils;
import cz.cvut.kbss.ear.service.GroupLectureService;
import cz.cvut.kbss.ear.service.RoomService;
import cz.cvut.kbss.ear.service.SportRoomReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/rooms")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
public class RoomController {

    private static final Logger LOG = LoggerFactory.getLogger(RoomController.class);

    private final RoomService service;
    private final SportRoomReservationService sportRoomReservationService;
    private final GroupLectureService lectureService;


    @Autowired
    public RoomController(RoomService roomService, SportRoomReservationService sportRoomReservationService, GroupLectureService lectureService) {
        this.service = roomService;
        this.sportRoomReservationService = sportRoomReservationService;
        this.lectureService = lectureService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Room> getRooms() {
        return service.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createRoom(@RequestBody Room room) {
        service.persist(room);
        LOG.debug("Created room {}.", room);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", room.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Room getById(@PathVariable Integer id) {
        final Room room = service.find(id);
        if (room == null) {
            throw NotFoundException.create("Room", id);
        }
        return room;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SportRoomReservation> getReservationsByRoom(@PathVariable Integer id) {
        return sportRoomReservationService.findAll(getById(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}/lectures", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GroupLecture> getGroupLecturesByRoom(@PathVariable Integer id) {
        return lectureService.findAll(getById(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/{id}/lectures", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addGroupLecture(@RequestBody GroupLecture lecture, @PathVariable Integer id) {
        final Room room = getById(id);
        lecture.setRoom(room);
        service.addGroupLectureToSchedule(lecture, room);
        LOG.debug("GroupLecture {} added to room {}.", lecture, room);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/{id}/reservations", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addSportRoomReservation(@RequestBody SportRoomReservation reservation, @PathVariable Integer id) {
        final Room room = getById(id);
        reservation.setSportRoom(room);
        service.addSportRoomReservation(reservation, room);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRoom(@PathVariable Integer id, @RequestBody Room room) {
        final Room original = getById(id);
        if (!original.getId().equals(room.getId())) {
            throw new ValidationException("Room identifier in the data does not match the one in the request URL.");
        }
        service.update(room);
        LOG.debug("Updated room {}.", room);
    }
}
