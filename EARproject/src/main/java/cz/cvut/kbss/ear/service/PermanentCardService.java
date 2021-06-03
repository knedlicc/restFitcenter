package cz.cvut.kbss.ear.service;

import cz.cvut.kbss.ear.dao.GroupLectureDao;
import cz.cvut.kbss.ear.dao.PermanentCardDao;
import cz.cvut.kbss.ear.dao.UserDao;
import cz.cvut.kbss.ear.model.PermanentCard;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.SportRoomReservation;
import cz.cvut.kbss.ear.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PermanentCardService {

    private final UserDao userDao;
    private final PermanentCardDao permanentCardDao;


    final User currentUser = new User(); // singleton simulating logged-in user

    @Autowired
    public PermanentCardService(UserDao userDao, PermanentCardDao permanentCardDao) {
        this.userDao = userDao;
        this.permanentCardDao = permanentCardDao;
    }

    @Transactional(readOnly = true)
    public PermanentCard find(Integer id) {
        return permanentCardDao.find(id);
    }

    @Transactional
    public void persist(PermanentCard permanentCard) {
        Objects.requireNonNull(permanentCard);
        permanentCardDao.persist(permanentCard);
    }

    @Transactional(readOnly = true)
    public List<PermanentCard> findAll() {
        return permanentCardDao.findAll();
    }

    @Transactional
    public void update(PermanentCard permanentCard) {
        Objects.requireNonNull(permanentCard);
        permanentCardDao.update(permanentCard);
    }


    @Transactional
    public void addPermanentCardToUser(User user, PermanentCard permanentCard) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(permanentCard);
        user.setCard(permanentCard);
        userDao.update(user);
    }

    @Transactional
    public void removePermanentCard(PermanentCard permanentCard){
        Objects.requireNonNull(permanentCard);
        permanentCardDao.remove(permanentCard);
    }


}
