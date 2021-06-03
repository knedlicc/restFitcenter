package cz.cvut.kbss.ear.rest;


import cz.cvut.kbss.ear.exception.NotFoundException;
import cz.cvut.kbss.ear.exception.ValidationException;
import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.PermanentCard;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.User;
import cz.cvut.kbss.ear.rest.util.RestUtils;
import cz.cvut.kbss.ear.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/permanent_cards")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
public class PermanentCardController {

    private static final Logger LOG = LoggerFactory.getLogger(RoomController.class);

    private final PermanentCardService service;
    private final UserService userService;

    public PermanentCardController(PermanentCardService permanentCardService, UserService userService){
        this.service = permanentCardService;
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PermanentCard> getCards() {
        return service.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCard(@RequestBody PermanentCard permanentCard) {
        service.persist(permanentCard);
        LOG.debug("Created PermanentCard {}.", permanentCard);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", permanentCard.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PermanentCard getById(@PathVariable Integer id) {
        final PermanentCard card = service.find(id);
        if (card == null) {
            throw NotFoundException.create("PermanentCard", id);
        }
        return card;
    }

//    @PutMapping(value = "/{id}/user", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void addPermanentCardToUser(@RequestBody User user, @PathVariable Integer id) {
//        final PermanentCard card = getById(id);
//        service.addPermanentCardToUser(user,card);
//        LOG.debug("Add permanent card {} to user {}", card, user);
//    }
//
@PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/permanent_cards/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void removePermanentCard(@PathVariable Integer id) {
        PermanentCard card = getById(id);
        if(card == null){
            throw NotFoundException.create("PermanentCard", id);
        }
        service.removePermanentCard(card);
        LOG.debug("Permanent card {} is removed", card);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCard(@PathVariable Integer id, @RequestBody PermanentCard card) {
        final PermanentCard original = getById(id);
        if (!original.getId().equals(card.getId())) {
            throw new ValidationException("PermanentCard identifier in the data does not match the one in the request URL.");
        }
        service.update(card);
        LOG.debug("Updated PermanentCard {}.", card);
    }

}
