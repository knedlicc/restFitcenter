package cz.cvut.kbss.ear.environment;

import cz.cvut.kbss.ear.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

public class Generator {

    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    public static SportCenter generateSportCenter() {
        final SportCenter s = new SportCenter();
        s.setName("Product" + randomInt());
        s.setAddress("Test address");
        s.setOpenFrom(LocalTime.MIN);
        s.setOpenTo(LocalTime.MAX);
        return s;
    }

    public static User generateUser() {
        final User u = new User();
        u.setName("test name" + randomInt());
        u.setBalance(BigDecimal.valueOf(10000));
        u.setEmail("dfdfdfd@gmail.com");
        u.setPassword("wW34234fff");
        u.setLectures(new ArrayList<>());
        return u;
    }

    public static GroupLecture generateGroupLecture() {
        final GroupLecture groupLecture = new GroupLecture();
        groupLecture.setId(randomInt());
        groupLecture.setOccupancy(0);
        groupLecture.setName("ExampleLecture");
        groupLecture.setPrice(new BigDecimal("100.0"));
        groupLecture.setUsersL(new ArrayList<>());
        groupLecture.setLectureCapacity(1);
        groupLecture.setStartTime(LocalDateTime.of(2020, 11, 15, 8, 15));
        groupLecture.setEndTime(LocalDateTime.of(2020, 11, 15, 15, 15));
        return groupLecture;
    }

    public static SportRoomReservation generateSportRoomReservation() {
        final SportRoomReservation reservation = new SportRoomReservation();
        return reservation;
    }

    public static PermanentCard generatePermanentCard() {
        final PermanentCard permanentCard = new PermanentCard();
        permanentCard.setId(randomInt());
        permanentCard.setCode(randomInt());
        permanentCard.setValidFrom(LocalDateTime.MIN);
        permanentCard.setValidTo(LocalDateTime.MAX);
        permanentCard.setValid(true);
        permanentCard.setUser(generateUser());
        return permanentCard;
    }
}
