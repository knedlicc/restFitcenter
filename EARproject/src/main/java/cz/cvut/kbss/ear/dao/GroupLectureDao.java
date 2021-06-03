package cz.cvut.kbss.ear.dao;


import cz.cvut.kbss.ear.model.*;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Repository
public class GroupLectureDao extends BaseDao<GroupLecture> {

    public GroupLectureDao() {
        super(GroupLecture.class);
    }

    public List<GroupLecture> findAll(Room room) {
        Objects.requireNonNull(room);
        return em.createNamedQuery("GroupLecture.findByRoom", GroupLecture.class).setParameter("room", room)
                .getResultList();
    }

    public List<GroupLecture> findLecturesOlderThanTwoYears() {
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime curYearDecreasedByTwoYears = time.minusYears(2);
        return em.createQuery("select g from GroupLecture g where g.startTime < (:pen)", GroupLecture.class).setParameter("pen", curYearDecreasedByTwoYears).getResultList();
    }

    public List<GroupLecture> findLecturesWithRequiredOccupation(Integer requiredOccupation){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GroupLecture> criteriaQuery = cb.createQuery(GroupLecture.class);

        Root<GroupLecture> root = criteriaQuery.from(GroupLecture.class);
        criteriaQuery.select(root).where(cb.greaterThanOrEqualTo(root.get(GroupLecture_.OCCUPANCY), requiredOccupation));

        TypedQuery<GroupLecture> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
