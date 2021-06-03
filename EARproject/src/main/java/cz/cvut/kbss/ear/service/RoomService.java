package cz.cvut.kbss.ear.service;

import cz.cvut.kbss.ear.dao.RoomDao;
import cz.cvut.kbss.ear.exception.BalanceIsNotEnoughException;
import cz.cvut.kbss.ear.exception.TimeIsNotAvailableException;
import cz.cvut.kbss.ear.model.*;
import cz.cvut.kbss.ear.util.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;

@Service
public class RoomService {

    private final RoomDao dao;
    private final SportRoomReservationService reservationService;


    @Autowired
    public RoomService(RoomDao dao, SportRoomReservationService reservationService) {
        this.dao = dao;
        this.reservationService = reservationService;
    }

    @Transactional(readOnly = true)
    public Room find(Integer id) {
        return dao.find(id);
    }

    @Transactional
    public void persist(Room room) {
        Objects.requireNonNull(room);
        dao.persist(room);
    }

    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return dao.findAll();
    }

    @Transactional
    public void addGroupLecture(GroupLecture groupLecture, Room room) {
        room.addGroupLecture(groupLecture);
        dao.update(room);
    }

    @Transactional
    public void persistSportRoomReservation(SportRoomReservation reservation, Room room) {
        room.addSportRoomReservation(reservation);
        dao.update(room);
    }

    @Transactional
    public void update(Room room) {
        Objects.requireNonNull(room);
        dao.update(room);
    }

    //administrator adds new groupLecture to the schedule
    @Transactional
    public void addGroupLectureToSchedule(GroupLecture lecture, Room room){
        Objects.requireNonNull(lecture);
        Objects.requireNonNull(room);
        if (Validation.timeIsValidForReservation(lecture.getStartTime(), lecture.getEndTime(), lecture.getRoom())) {
            if (Validation.durationIsLongEnough(lecture)) {
                if (lecture.getCapacity() >= 1) {
                    lecture.setOccupancy(0);
                    addGroupLecture(lecture, room);
                    if (lecture.isRecurring()) {
                        addGroupLectureToFollowingDaysUntilEndOfYear(lecture, room);
                    }
                } else throw new TimeIsNotAvailableException("Please set lecture capacity to at least 1");
            } else
                throw new TimeIsNotAvailableException("This duration is too short, please increase it to at least 30 minutes");
        } else throw new TimeIsNotAvailableException("This time is not available");
    }

    public void addGroupLectureToFollowingDaysUntilEndOfYear(GroupLecture lecture, Room room) {
        GroupLecture curGroupLecture = new GroupLecture(lecture);
        while (true) {
            curGroupLecture = computeNextDate(curGroupLecture);
            if (curGroupLecture.getStartTime().getYear() != lecture.getStartTime().getYear()) break;
            addGroupLecture(curGroupLecture, room);
        }
    }

    public GroupLecture computeNextDate(GroupLecture lecture) {
        DayOfWeek lectureDay = lecture.getStartTime().getDayOfWeek();
        GroupLecture curGroupLecture = new GroupLecture(lecture);

        LocalDateTime nextLectureStartTime = curGroupLecture.getStartTime().with(TemporalAdjusters.next(lectureDay)); //iterate to the next week same day
        LocalDateTime nextLectureEndTime = curGroupLecture.getStartTime().with(TemporalAdjusters.next(lectureDay));

        curGroupLecture.setStartTime(nextLectureStartTime);
        curGroupLecture.setEndTime(nextLectureEndTime);

        return curGroupLecture;
    }

    public void addSportRoomReservation(SportRoomReservation reservation, Room room) {
        Objects.requireNonNull(reservation);
        Objects.requireNonNull(room);
        if (Validation.timeIsValidForReservation(reservation.getStartTime(), reservation.getEndTime(), reservation.getSportRoom())) {
            if (Validation.durationIsLongEnough(reservation)) {
                BigDecimal actualReservationPrice = reservationService.computeActualReservationPrice(reservation);
                reservation.setPrice(actualReservationPrice);
                if (Validation.userBalanceIsEnough(reservation.getPrice(), reservation.getOwner())) {
                    persistSportRoomReservation(reservation, room);
                    if (reservation.getOwner().getRole() == Roles.USER) {
                        reservationService.payForSportRoomReservation(reservation, actualReservationPrice);
                    }
                } else
                    throw new BalanceIsNotEnoughException("User balance is not enough for creating this reservation");
            } else
                throw new TimeIsNotAvailableException("This duration is too short, please increase it to at least 30 minutes");
        } else throw new TimeIsNotAvailableException("This time is not available");
    }

    public void removeSportRoomReservation(SportRoomReservation reservation) {
        Objects.requireNonNull(reservation);
        reservationService.deleteReservation(reservation);
        User user = reservation.getOwner();
        if (user.getRole().toString().equals("ROLE_USER")) {
            reservationService.returnMoneyForRemovedSportRoomReservation(reservation);
        }
    }
}
