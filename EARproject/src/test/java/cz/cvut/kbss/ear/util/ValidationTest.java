package cz.cvut.kbss.ear.util;

import cz.cvut.kbss.ear.SportReservationSystemApplication;
import cz.cvut.kbss.ear.environment.Generator;
import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.SportCenter;
import cz.cvut.kbss.ear.model.User;
import cz.cvut.kbss.ear.service.GroupLectureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@Transactional
@TestPropertySource(locations = "classpath:/application-test.properties")
@SpringBootTest(classes = SportReservationSystemApplication.class)
public class ValidationTest {
    GroupLecture groupLecture;
    User user = Generator.generateUser();

    @Mock
    Room roomMock;

    @Mock
    SportCenter sportCenterMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(roomMock.getSportCenter()).thenReturn(sportCenterMock);
        when(roomMock.getRoomsReservations()).thenReturn(new ArrayList<>());
        setSportCenterOpenHours();
    }

    @Autowired
    private GroupLectureService sut;

    @Test
    public void addGroupLectureToScheduleReturnFalseWhenTimeIsAlreadyBooked() {
        GroupLecture groupLecture = Generator.generateGroupLecture();
        List<GroupLecture> reservations = new ArrayList<>();
        reservations.add(groupLecture);
        when(roomMock.getLecturesReservations()).thenReturn(reservations);

        /*try to add reservation to a schedule that already contains a reservation for this time*/
        boolean result = Validation.timeIsValidForReservation(groupLecture.getStartTime(), groupLecture.getEndTime(), roomMock);

        assertFalse(result);
    }

    public void setSportCenterOpenHours() {
        when(sportCenterMock.getOpenFrom()).thenReturn(LocalTime.of(8, 15, 15));
        when(sportCenterMock.getOpenTo()).thenReturn(LocalTime.of(20, 15, 15));
    }
}
