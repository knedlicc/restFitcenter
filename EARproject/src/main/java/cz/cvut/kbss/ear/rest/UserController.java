package cz.cvut.kbss.ear.rest;

import cz.cvut.kbss.ear.exception.NotFoundException;
import cz.cvut.kbss.ear.exception.ValidationException;
import cz.cvut.kbss.ear.model.*;
import cz.cvut.kbss.ear.rest.util.RestUtils;
import cz.cvut.kbss.ear.security.model.AuthT;
import cz.cvut.kbss.ear.service.GroupLectureService;
import cz.cvut.kbss.ear.service.PermanentCardService;
import cz.cvut.kbss.ear.service.RoomService;
import cz.cvut.kbss.ear.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/rest/users")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService service;
    private final RoomService roomService;
    private final GroupLectureService lectureService;
    private final PermanentCardService permanentCardService;


    @Autowired
    public UserController(UserService service, RoomService roomService, GroupLectureService lectureService, PermanentCardService permanentCardService) {
        this.service = service;
        this.roomService = roomService;
        this.lectureService = lectureService;
        this.permanentCardService = permanentCardService;
    }

    @PreAuthorize("(!#user.isAdmin() && anonymous) || hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/register",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody User user) {
        service.persist(user);
        LOG.debug("User {} successfully registered.", user);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getCurrent(Principal principal) {
        final AuthT auth = (AuthT) principal;
        return auth.getPrincipal().getUser();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable Integer id, @RequestBody User user) {
        final User original = getById(id);
        if (!original.getId().equals(user.getId())) {
            throw new ValidationException("User identifier in the data does not match the one in the request URL.");
        }
        service.update(user);
        LOG.debug("Updated User {}.", user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getById(@PathVariable Integer id) {
        final User reservation = service.find(id);
        if (reservation == null) {
            throw NotFoundException.create("SportRoomReservation", id);
        }
        return reservation;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/{id}/lectures/{lecture_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void registerForLecture(@PathVariable Integer lecture_id, @PathVariable Integer id) {
        final User user = getById(id);
        final GroupLecture lecture = lectureService.find(lecture_id);
        lecture.addUser(user);
        service.registerForLecture(lecture, user);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/{id}/reservations/{room_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addReservation(@RequestBody SportRoomReservation reservation, @PathVariable Integer id, @PathVariable Integer room_id) {
        final User user = getById(id);
        final Room room = roomService.find(room_id);
        reservation.addUser(user);
        reservation.setSportRoom(room);
        roomService.addSportRoomReservation(reservation, reservation.getSportRoom());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SportRoomReservation> getReservations(Principal principal, @PathVariable Integer id) {
        final User user = getById(id);
        return user.getRoomReservations();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}/lectures", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GroupLecture> getGroupLectures(@PathVariable Integer id) {
        final User user = getById(id);
        return user.getLectures();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/users_with_greater_balance/{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getUsersWithBalanceMoreThan(@PathVariable BigDecimal amount) {
        return service.getUsersWithGreaterBalance(amount);
    }

//    @GetMapping(value = "/registered_alone", produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<User> getUsersRegisteredAloneToLectures() {
//        return service.findUsersRegisteredAloneToLectures();
//    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}/card", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addPermanentCardToUser(@RequestBody PermanentCard permanentCard, @PathVariable Integer id) {
        final User user = getById(id);
        permanentCardService.addPermanentCardToUser(user,permanentCard);
        LOG.debug("Add to user {} the permanentCard {}", user, permanentCard);
    }
}
