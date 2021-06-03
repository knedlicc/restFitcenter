package cz.cvut.kbss.ear.rest;

import cz.cvut.kbss.ear.exception.NotFoundException;
import cz.cvut.kbss.ear.exception.ValidationException;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.SportCenter;
import cz.cvut.kbss.ear.rest.util.RestUtils;
import cz.cvut.kbss.ear.service.SportCenterService;
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
@RequestMapping("/rest/sport_center")
public class SportCenterController {


    private static final Logger LOG = LoggerFactory.getLogger(RoomController.class);

    private final SportCenterService service;

    @Autowired
    public SportCenterController(SportCenterService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SportCenter> getSportCenters() {
        return service.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createSportCenter(@RequestBody SportCenter sportCenter) {
        service.persist(sportCenter);
        LOG.debug("Created room {}.", sportCenter);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", sportCenter.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSportCenter(@PathVariable Integer id, @RequestBody SportCenter sportCenter) {
        final SportCenter original = getById(id);
        if (!original.getId().equals(sportCenter.getId())) {
            throw new ValidationException("SportCenter identifier in the data does not match the one in the request URL.");
        }
        service.update(sportCenter);
        LOG.debug("Updated SportCenter {}.", sportCenter);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SportCenter getById(@PathVariable Integer id) {
        final SportCenter sportCenter = service.find(id);
        if (sportCenter == null) {
            throw NotFoundException.create("SportCenter", id);
        }
        return sportCenter;
    }

}
