package cz.cvut.kbss.ear.dao;

import cz.cvut.kbss.ear.exception.PersistenceException;
import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.SportRoomReservation;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Repository
public class SportRoomReservationDao extends BaseDao<SportRoomReservation>{

    public SportRoomReservationDao(){
        super(SportRoomReservation.class);
    }


    public List<SportRoomReservation> findAll(Room room) {
        Objects.requireNonNull(room);
        return em.createNamedQuery("SportRoomReservation.findByRoom", SportRoomReservation.class).setParameter("room", room)
                .getResultList();
    }
    public List<SportRoomReservation> findReservationsOlderThanTwoYears() {
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime curYearDecreasedByTwoYears = time.minusYears(2);
        return em.createQuery("select r from SportRoomReservation r where r.startTime < (:prev)", SportRoomReservation.class).setParameter("prev", curYearDecreasedByTwoYears).getResultList();
    }
}
