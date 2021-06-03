package cz.cvut.kbss.ear.rest;


import cz.cvut.kbss.ear.exception.NotFoundException;
import cz.cvut.kbss.ear.exception.ValidationException;
import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.User;
import cz.cvut.kbss.ear.service.GroupLectureService;
import cz.cvut.kbss.ear.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/lectures")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
public class GroupLectureController {
    private static final Logger LOG = LoggerFactory.getLogger(GroupLecture.class);
    private final GroupLectureService service;
    private final UserService userService;

    @Autowired
    public GroupLectureController(GroupLectureService groupLectureService, UserService userService) {
        this.service = groupLectureService;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GroupLecture> getGroupLectures() {
        return service.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GroupLecture getById(@PathVariable Integer id) {
        final GroupLecture lecture = service.find(id);
        if (lecture == null) {
            throw NotFoundException.create("GroupLecture", id);
        }
        return lecture;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}/lecture_attendees", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getLectureAttendees(@PathVariable Integer id) {
        return userService.findAll(getById(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupLecture(@PathVariable Integer id) {
        final GroupLecture lectureToRemove = getById(id);
        if (lectureToRemove == null) {
            throw NotFoundException.create("GroupLecture", id);
        }
        service.removeGroupLecture(lectureToRemove);
        LOG.debug("GroupLecture {} removed from the schedule, attendees got their money back", lectureToRemove);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/delete_old_lectures")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOldLectures() {
        service.removeLecturesOlderThan2Years();
        LOG.debug("Lectures older than 2 years were removed");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGroupLecture(@PathVariable Integer id, @RequestBody GroupLecture lecture) {
        final GroupLecture original = getById(id);
        if (!original.getId().equals(lecture.getId())) {
            throw new ValidationException("GroupLecture identifier in the data does not match the one in the request URL.");
        }
        service.update(lecture);
        LOG.debug("Updated GroupLecture {}.", lecture);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/required_occupation/{o}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GroupLecture> getLecturesWithRequiredOccupation(@PathVariable Integer o) {
        return service.findLecturesWithRequiredOccupation(o);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{lecture_id}/lecture_attendees/{user_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroupLectureForClient(@PathVariable Integer lecture_id,@PathVariable Integer user_id) {
        final GroupLecture lectureToRemove = getById(lecture_id);
        final User userToRemove = userService.find(user_id);
        if (lectureToRemove == null) {
            throw NotFoundException.create("GroupLecture", lecture_id);
        }
        if(userToRemove == null){
            throw NotFoundException.create("User", user_id);
        }
        service.removeLectureReservationForClient(lectureToRemove,userToRemove);
        LOG.debug("GroupLecture {} removed from the {}, attendee got his money back", lectureToRemove,userToRemove);
    }

}
