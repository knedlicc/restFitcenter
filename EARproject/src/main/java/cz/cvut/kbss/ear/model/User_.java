package cz.cvut.kbss.ear.model;

import javax.persistence.metamodel.SingularAttribute;
import java.math.BigDecimal;

public abstract class User_ {
    public static volatile SingularAttribute<User, BigDecimal> balance;
//    public static volatile SingularAttribute<User, GroupLecture> lectures;

    public static final String BALANCE = "balance";
    public static final String LECTURES = "lectures";
}
