package cz.cvut.kbss.ear.dao;

import cz.cvut.kbss.ear.model.PermanentCard;
import org.springframework.stereotype.Repository;

@Repository
public class CardDao extends BaseDao<PermanentCard> {

    public CardDao(){
        super(PermanentCard.class);
    }
}
