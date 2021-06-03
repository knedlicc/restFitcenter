package cz.cvut.kbss.ear.service;

import cz.cvut.kbss.ear.dao.RoomDao;
import cz.cvut.kbss.ear.dao.SportCenterDao;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.SportCenter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SportCenterService {
    private RoomDao roomDao;
    private SportCenterDao sportCenterDao;

    public SportCenterService(RoomDao dao, SportCenterDao sportCenterDao){
        this.roomDao = dao;
        this.sportCenterDao = sportCenterDao;
    }

    @Transactional(readOnly = true)
    public SportCenter find(Integer id) {
        return sportCenterDao.find(id);
    }

    @Transactional
    public void persist(SportCenter sportCenter) {
        Objects.requireNonNull(sportCenter);
        sportCenterDao.persist(sportCenter);
    }

    @Transactional(readOnly = true)
    public List<SportCenter> findAll() {
        return sportCenterDao.findAll();
    }

    @Transactional
    public void addRoom(Room room, SportCenter sportCenter) {
        Objects.requireNonNull(room);
        Objects.requireNonNull(sportCenter);
        sportCenter.addRoom(room);
        sportCenterDao.update(sportCenter);
    }

    @Transactional
    public void removeRoom(Room room, SportCenter sportCenter) {
        Objects.requireNonNull(room);
        Objects.requireNonNull(sportCenter);
        sportCenter.removeRoom(room);
        sportCenterDao.update(sportCenter);
    }

    @Transactional
    public void update(SportCenter sportCenter) {
        Objects.requireNonNull(sportCenter);
        sportCenterDao.update(sportCenter);
    }
}
