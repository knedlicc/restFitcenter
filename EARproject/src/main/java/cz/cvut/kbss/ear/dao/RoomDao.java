package cz.cvut.kbss.ear.dao;

import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.Room;
import org.springframework.stereotype.Repository;

import java.time.Period;
import java.util.Objects;

@Repository
public class RoomDao extends BaseDao<Room>{

    public RoomDao(){
        super(Room.class);
    }

    /*public void getFreeTime(Room room) {
        Objects.requireNonNull(room);
        return em.createQuery("select r from Room r wh")
        return em.createNamedQuery("GroupLecture.findAvailableTime", Room.class).setParameter("room", room)
                .getFirstResult();
    } */
}
