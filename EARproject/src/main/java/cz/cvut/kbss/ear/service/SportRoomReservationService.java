package cz.cvut.kbss.ear.service;


import cz.cvut.kbss.ear.dao.RoomDao;
import cz.cvut.kbss.ear.dao.SportRoomReservationDao;
import cz.cvut.kbss.ear.dao.UserDao;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.SportRoomReservation;
import cz.cvut.kbss.ear.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
public class SportRoomReservationService {
    private SportRoomReservationDao dao;
    private RoomDao roomDao;
    private UserDao userDao;

    public SportRoomReservationService() {
    }

    public SportRoomReservationService(SportRoomReservationDao dao) {
        this.dao = dao;
    }

    @Autowired
    public SportRoomReservationService(SportRoomReservationDao dao, RoomDao roomDao, UserDao client) {
        this.dao = dao;
        this.roomDao = roomDao;
        this.userDao = client;
    }

    @Transactional(readOnly = true)
    public List<SportRoomReservation> findAll() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public List<SportRoomReservation> findAll(Room room) {
        return dao.findAll(room);
    }

    @Transactional(readOnly = true)
    public SportRoomReservation find(Integer id) {
        return dao.find(id);
    }

    @Transactional
    public void deleteReservation(SportRoomReservation reservation) {
        Objects.requireNonNull(reservation);
        dao.remove(reservation);
        User user = reservation.getOwner();
        if (user.getRole().toString().equals("ROLE_USER")) {
            returnMoneyForRemovedSportRoomReservation(reservation);
        }
        userDao.update(user);
    }

    @Transactional
    public void removeReservationsOlderThan2Years() {
        List<SportRoomReservation> oldReservations = dao.findReservationsOlderThanTwoYears();
        for (SportRoomReservation r : oldReservations) {
            dao.remove(r);
        }
    }

    public void returnMoneyForRemovedSportRoomReservation(SportRoomReservation reservation) {
        User reservationOwner = reservation.getOwner();
        BigDecimal balance = reservationOwner.getBalance();
        Objects.requireNonNull(reservation);
        reservationOwner.setBalance(balance.add(reservation.getPrice()));
        userDao.update(reservationOwner);
    }

    public BigDecimal computeActualReservationPrice(SportRoomReservation reservation) {
        long reservationDurationInMinutes = Duration.between(reservation.getStartTime(), reservation.getEndTime()).toMinutes();
        BigDecimal reservationPriceForHour = reservation.getSportRoom().getPriceForHourReservation();
        BigDecimal priceForMinute = reservationPriceForHour.divide(new BigDecimal("60.0"), 2, RoundingMode.DOWN);
        return priceForMinute.multiply(BigDecimal.valueOf(reservationDurationInMinutes));
    }

    public void payForSportRoomReservation(SportRoomReservation reservation, BigDecimal reservationPrice) {
        BigDecimal curBalance = reservation.getOwner().getBalance();
        reservation.getOwner().setBalance(curBalance.subtract(reservationPrice));
    }

    @Transactional
    public void update(SportRoomReservation sportRoomReservation) {
        Objects.requireNonNull(sportRoomReservation);
        dao.update(sportRoomReservation);
    }
}
