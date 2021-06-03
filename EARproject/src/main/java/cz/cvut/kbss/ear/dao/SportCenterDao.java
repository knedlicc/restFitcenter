package cz.cvut.kbss.ear.dao;

import cz.cvut.kbss.ear.model.SportCenter;
import org.springframework.stereotype.Repository;

@Repository
public class SportCenterDao extends BaseDao<SportCenter> {

    public SportCenterDao(){
        super(SportCenter.class);
    }
}
