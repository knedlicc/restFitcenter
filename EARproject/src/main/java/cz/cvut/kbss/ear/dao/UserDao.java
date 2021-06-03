package cz.cvut.kbss.ear.dao;

import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.GroupLecture_;
import cz.cvut.kbss.ear.model.User;
import cz.cvut.kbss.ear.model.User_;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Repository
public class UserDao extends BaseDao<User> {

    public UserDao() {
        super(User.class);
    }

    public User findByEmail(String email) {
        try {
            return em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<User> findAll(GroupLecture groupLecture) {
        Objects.requireNonNull(groupLecture);
        return em.createNamedQuery("User.findByGroupLecture", User.class).setParameter("groupLecture", groupLecture)
                .getResultList();
    }

    public List<User> findUsersWithGreaterBalance(BigDecimal balance) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = cb.createQuery(User.class);

        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root).where(cb.greaterThan(root.get(User_.BALANCE), balance));

        TypedQuery<User> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    //return list of users which are registered to lectures with occupancy = 1 (only one user)
    public List<User> findUsersRegisteredAloneToLectures(){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        Join<GroupLecture, User> lecture = root.join(User_.LECTURES);

        ParameterExpression<Integer> occupancy = cb.parameter(Integer.class);
        cq.where(cb.equal(lecture.get(GroupLecture_.OCCUPANCY), occupancy));

        TypedQuery<User> query = em.createQuery(cq);

        return query.getResultList();
    }
}
