package cz.cvut.kbss.ear.service;


import cz.cvut.kbss.ear.SportReservationSystemApplication;
import cz.cvut.kbss.ear.dao.GroupLectureDao;
import cz.cvut.kbss.ear.dao.UserDao;
import cz.cvut.kbss.ear.environment.Generator;
import cz.cvut.kbss.ear.exception.CapacityIsNotEnoughException;
import cz.cvut.kbss.ear.exception.ClientBalanceIsNotEnough;
import cz.cvut.kbss.ear.exception.TimeIsNotAvailableException;
import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.PermanentCard;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SportReservationSystemApplication.class)
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
public class GroupLectureServiceTest {
    @Mock
    GroupLectureDao lectureDaoMock;
    @Mock
    UserDao userDaoMock;
    @Mock
    Room roomMock;
    @Mock
    PermanentCard validCardMock;
    @Mock
    PermanentCard invalidCardMock;

    GroupLecture groupLecture;
    User user;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private GroupLectureService sut;

    @Autowired
    private RoomService s;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(validCardMock.getValid()).thenReturn(true);
        when(validCardMock.isCardValid()).thenReturn(true);
        when(invalidCardMock.getValid()).thenReturn(false);
        when(invalidCardMock.isCardValid()).thenReturn(false);


        when(roomMock.getCapacity()).thenReturn(1);

        this.user = Generator.generateUser();

        this.sut = new GroupLectureService(lectureDaoMock, userDaoMock);
    }

    @Test
    public void registerForLectureIncreasesOccupancy() {
        this.groupLecture = Generator.generateGroupLecture();
        int initialOccupancy = 0;
        groupLecture.setOccupancy(initialOccupancy);
        groupLecture.setRoom(roomMock);
        user.setCard(validCardMock);

        sut.registerForLecture(groupLecture, user);

        assertEquals(initialOccupancy + 1, groupLecture.getOccupancy());
    }

    @Test
    public void registerForLectureAddsUserToLectureList() {
        this.groupLecture = Generator.generateGroupLecture();
        groupLecture.setUsersL(new ArrayList<>());
        groupLecture.setRoom(roomMock);
        PermanentCard card = new PermanentCard(1, LocalDateTime.of(2021, 7, 1, 8, 0), LocalDateTime.of(2021, 9, 1, 8, 0), user, true);
        user.setCard(card);

        sut.registerForLecture(groupLecture, user);
        em.persist(groupLecture);
        final GroupLecture result = em.find(GroupLecture.class, groupLecture.getId());

        assertTrue(result.getUsersL().contains(user));
    }

    @Test //with invalid card
    public void registerForLectureDecreasesUserBalanceByLecturePrice() {
        this.groupLecture = Generator.generateGroupLecture();
        groupLecture.setRoom(roomMock);
        groupLecture.setPrice(new BigDecimal("100.0"));
        user.setBalance(new BigDecimal("300.0"));
        user.setCard(invalidCardMock);

        sut.registerForLecture(groupLecture, user);

        assertEquals(new BigDecimal("200.0"), user.getBalance());
    }


    @Test
    public void registerForLectureThrowsCapacityIsNotEnoughExceptionWhenLectureIsFull() {
        thrown.expect(CapacityIsNotEnoughException.class);
        this.groupLecture = Generator.generateGroupLecture();
        groupLecture.setRoom(roomMock);
        when(roomMock.getCapacity()).thenReturn(1);
        groupLecture.setOccupancy(1);

        sut.registerForLecture(groupLecture, user);
    }

    @Test //with invalid card
    public void registerForLectureThrowsClientBalanceIsNotEnoughExceptionWhenLecturesPriceIsHigherThanUserBalance() {
        thrown.expect(ClientBalanceIsNotEnough.class);
        this.groupLecture = Generator.generateGroupLecture();
        groupLecture.setRoom(roomMock);
        user.setBalance(new BigDecimal("100.0"));
        user.setCard(invalidCardMock);
        groupLecture.setPrice(new BigDecimal("300.0"));

        sut.registerForLecture(groupLecture, user);
    }

    @Test
    public void addGroupLectureToScheduleThrowsTimeIsNotAvailableExceptionWhenTimeIsOccupied() throws Exception {
        thrown.expect(TimeIsNotAvailableException.class);
        this.groupLecture = Generator.generateGroupLecture();
        List<GroupLecture> reservations = new ArrayList<>();
        groupLecture.setStartTime(LocalDateTime.of(2020, 11, 15, 8, 15));
        groupLecture.setEndTime(LocalDateTime.of(2020, 11, 15, 15, 15));
        when(roomMock.getRoomsReservations()).thenReturn(new ArrayList<>());
        when(roomMock.getLecturesReservations()).thenReturn(reservations);
        reservations.add(groupLecture);
        groupLecture.setRoom(roomMock);

        s.addGroupLectureToSchedule(groupLecture, roomMock); //try to add groupLecture to database
    }

}