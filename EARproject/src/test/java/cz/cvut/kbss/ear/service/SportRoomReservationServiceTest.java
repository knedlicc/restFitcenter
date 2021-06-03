package cz.cvut.kbss.ear.service;

import cz.cvut.kbss.ear.SportReservationSystemApplication;
import cz.cvut.kbss.ear.dao.SportRoomReservationDao;
import cz.cvut.kbss.ear.dao.UserDao;
import cz.cvut.kbss.ear.environment.Generator;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.SportCenter;
import cz.cvut.kbss.ear.model.SportRoomReservation;
import cz.cvut.kbss.ear.model.User;
import cz.cvut.kbss.ear.util.Validation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

//@RunWith(SpringRunner.class)
//@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@SpringBootTest(classes = SportReservationSystemApplication.class)
@Transactional
@RunWith(SpringRunner.class)
//@PowerMockRunnerDelegate(SpringRunner.class)
@TestPropertySource(locations = "classpath:/application-test.properties")
@PrepareForTest(Validation.class)
public class SportRoomReservationServiceTest {

    @Mock
    SportRoomReservationDao reservationDaoMock;
    @Mock
    UserDao userDaoMock;
    @Mock
    Room roomMock;
    @Mock
    SportCenter sportCenterMock;
    @Mock
    User userMock;

    SportRoomReservationService sportRoomReservationService = new SportRoomReservationService(reservationDaoMock);

    SportRoomReservation reservation;
    User user;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SportRoomReservationService sut;

    @Autowired
    private RoomService s;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reservation = Generator.generateSportRoomReservation();
        this.sut = new SportRoomReservationService(reservationDaoMock);
    }

    @Test
    public void createSportRoomReservationComputesReservationPriceCorrectly() {
        Room room = new Room();
        room.setGroupLectures(new ArrayList<>());
        room.setSportRoomReservations(new ArrayList<>());
        room.setPriceForHourReservation(new BigDecimal("600.0"));
        reservation.setStartTime(LocalDateTime.of(2020, 10, 15, 10, 0));
        reservation.setEndTime(LocalDateTime.of(2020, 10, 15, 10, 40));
        reservation.setSportRoom(room);

        BigDecimal reservationPrice = sportRoomReservationService.computeActualReservationPrice(reservation);

        assertEquals(new BigDecimal("400.00"), reservationPrice);
    }
}
