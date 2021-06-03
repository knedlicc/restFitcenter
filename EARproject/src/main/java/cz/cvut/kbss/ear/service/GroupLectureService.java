package cz.cvut.kbss.ear.service;

import cz.cvut.kbss.ear.dao.GroupLectureDao;
import cz.cvut.kbss.ear.dao.UserDao;
import cz.cvut.kbss.ear.exception.CapacityIsNotEnoughException;
import cz.cvut.kbss.ear.exception.ClientBalanceIsNotEnough;
import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.Roles;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static cz.cvut.kbss.ear.util.Validation.capacityIsEnoughForSignUp;
import static cz.cvut.kbss.ear.util.Validation.userBalanceIsEnough;

@Service
public class GroupLectureService {
    private final GroupLectureDao dao;
    private final UserDao userDao;

    @Autowired
    public GroupLectureService(GroupLectureDao dao, UserDao userDao) {
        this.dao = dao;
        this.userDao = userDao;
    }

    @Transactional(readOnly = true)
    public List<GroupLecture> findAll() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public List<GroupLecture> findAll(Room room) {
        return dao.findAll(room);
    }

    @Transactional(readOnly = true)
    public GroupLecture find(Integer id) {
        return dao.find(id);
    }

    //user registers for an existing course in the schedule
    @Transactional
    public void registerForLecture(GroupLecture groupLecture, User user) {
        Objects.requireNonNull(groupLecture);
        Objects.requireNonNull(user);
        if (capacityIsEnoughForSignUp(groupLecture) && user.getCard().isCardValid()) {
            user.registerForLecture(groupLecture);
            groupLecture.setOccupancy(groupLecture.getOccupancy() + 1);
            dao.update(groupLecture);
        } else if (capacityIsEnoughForSignUp(groupLecture) && userBalanceIsEnough(groupLecture.getPrice(), user)) {
            user.registerForLecture(groupLecture);
            payForGroupLecture(groupLecture, user);
            groupLecture.setOccupancy(groupLecture.getOccupancy() + 1);
            dao.update(groupLecture);
        } else if (!capacityIsEnoughForSignUp(groupLecture)) {
            throw new CapacityIsNotEnoughException("The lecture's capacity is not enough for registering in this lecture");
        } else {
            throw new ClientBalanceIsNotEnough("The client's balance is not enough for registering in this lecture");
        }
    }

    @Transactional
    public List<GroupLecture> findLecturesWithRequiredOccupation(Integer requiredOccupation) {
        return dao.findLecturesWithRequiredOccupation(requiredOccupation);
    }

    @Transactional
    public void removeLectureReservationForClient(GroupLecture groupLecture, User user) {
        Objects.requireNonNull(groupLecture);
        Objects.requireNonNull(user);
        user.removeGroupLecture(groupLecture);
        if (user.getRole().equals(Roles.USER) && !user.getCard().isCardValid()) {
            returnMoneyForCancelledGroupLectureReservation(groupLecture, user);
        }
        groupLecture.setOccupancy(groupLecture.getOccupancy() - 1);
        dao.update(groupLecture);
    }

    @Transactional
    public void removeGroupLecture(GroupLecture groupLecture) {
        Objects.requireNonNull(groupLecture);
        dao.remove(groupLecture);
        if (!groupLecture.getUsersL().isEmpty()) {
            for (User user : groupLecture.getUsersL()) {
                if (!user.getCard().isCardValid()) {
                    returnMoneyToClients(groupLecture);
                }
            }
        }
    }

    @Transactional
    public void removeLecturesOlderThan2Years() {
        List<GroupLecture> lectures = dao.findLecturesOlderThanTwoYears();
        for (GroupLecture l : lectures) {
            dao.remove(l);
        }
    }

//    //administrator adds new groupLecture to the schedule
//    @Transactional
//    public void addGroupLectureToSchedule(GroupLecture groupLecture) {
//        Objects.requireNonNull(groupLecture);
//        if (groupLecture.isRecurring()) {
//            addRecurringGroupLecture(groupLecture);
//        } else addGroupLecture(groupLecture);
//    }
//
//    //add groupLecture to the date
//    public void addGroupLecture(GroupLecture groupLecture) {
//        if (Validation.timeIsValidForReservation(groupLecture.getStartTime(), groupLecture.getEndTime(), groupLecture.getRoom()))
//            dao.persist(groupLecture);
//        else throw new TimeIsNotAvailableException("This time is not available");
//    }
//
//    public void addRecurringGroupLecture(GroupLecture groupLecture) {
//        addGroupLecture(groupLecture);
//        addGroupLectureToFollowingDaysUntilEndOfYear(groupLecture);
//    }
//

//    public void addGroupLectureToFollowingDaysUntilEndOfYear(GroupLecture groupLecture) {
//        LocalDateTime lectureDateTime = groupLecture.getStartTime();
//        DayOfWeek lectureDay = lectureDateTime.getDayOfWeek();
//        int lectureMonth = lectureDateTime.getMonthValue();
//
//        GroupLecture curGroupLecture = new GroupLecture(groupLecture);
//
//        for (int i = lectureMonth; i <= 12; i++) {
//            while (curGroupLecture.getStartTime().getDayOfMonth() <= 31) { // adds a lecture to every week with the preassigned day in a month
//                LocalDateTime nextLectureStartTime = curGroupLecture.getStartTime().with(TemporalAdjusters.next(lectureDay)); //iterate to the next week same day
//                LocalDateTime nextLectureEndTime = curGroupLecture.getStartTime().with(TemporalAdjusters.next(lectureDay));
//
//                curGroupLecture.setStartTime(nextLectureStartTime);
//                curGroupLecture.setEndTime(nextLectureEndTime);
//                dao.persist(curGroupLecture);
//            }
//            curGroupLecture.setStartTime(curGroupLecture.getStartTime().withDayOfMonth(1)); //groupLecture for the next month will be added from the first-week starting
//        }
//    }
//
//    //administrator removes the lecture from the schedule; returns money to registered on this lecture clients
//    @Transactional
//    public void removeLectureFromSchedule(GroupLecture groupLecture) {
//        Objects.requireNonNull(groupLecture);
//        dao.remove(groupLecture);
//        if (!groupLecture.getUsersL().isEmpty()) returnMoneyToClients(groupLecture);
//    }

    public void payForGroupLecture(GroupLecture groupLecture, User user) {
        BigDecimal curBalance = user.getBalance();
        user.setBalance(curBalance.subtract(groupLecture.getPrice()));
        userDao.update(user);
    }

    public void returnMoneyForCancelledGroupLectureReservation(GroupLecture groupLecture, User user) {
        BigDecimal balance = user.getBalance();
        user.setBalance(balance.add(groupLecture.getPrice()));
        userDao.update(user);
    }

    public void returnMoneyToClients(GroupLecture groupLecture) {
        List<User> visitors = groupLecture.getUsersL();
        for (User user : visitors) {
            returnMoneyForCancelledGroupLectureReservation(groupLecture, user);
        }
    }

    @Transactional
    public void update(GroupLecture groupLecture) {
        Objects.requireNonNull(groupLecture);
        dao.update(groupLecture);
    }


}