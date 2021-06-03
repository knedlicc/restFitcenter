package cz.cvut.kbss.ear.dao;

import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.PermanentCard;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class PermanentCardDao extends BaseDao<PermanentCard>{


    public PermanentCardDao(){
        super(PermanentCard.class);
    }

    public PermanentCard find(User user) {
        Objects.requireNonNull(user);
        return em.createNamedQuery("PermanentCard.findByUser", PermanentCard.class).setParameter("user", user)
                .getSingleResult();
    }
}
