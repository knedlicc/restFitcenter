package cz.cvut.kbss.ear.service;

import cz.cvut.kbss.ear.dao.GroupLectureDao;
import cz.cvut.kbss.ear.dao.UserDao;
import cz.cvut.kbss.ear.exception.CapacityIsNotEnoughException;
import cz.cvut.kbss.ear.exception.ClientBalanceIsNotEnough;
import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.User;
import cz.cvut.kbss.ear.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static cz.cvut.kbss.ear.util.Validation.capacityIsEnoughForSignUp;
import static cz.cvut.kbss.ear.util.Validation.userBalanceIsEnough;

@Service("myUserService")
public class UserService {

    private final UserDao dao;
    private final GroupLectureDao lectureDao;
    private final GroupLectureService lectureService;
    private final PasswordEncoder passwordEncoder;

//    private final Map<String, UserDetails> repository = new HashMap<>();
//
//    {
//        UserDetails userAdmin = new org.springframework.security.core.userdetails.User("admin", "123", Collections.singleton(new SimpleGrantedAuthority("ADMIN")));
//        UserDetails userSimple = new org.springframework.security.core.userdetails.User("user", "pass", Collections.singleton(new SimpleGrantedAuthority("USER")));
//        repository.put(userAdmin.getUsername(), userAdmin);
//        repository.put(userSimple.getUsername(), userSimple);
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return repository.get(username);
//    }

    final User currentUser = new User(); // singleton simulating logged-in user

    @Autowired
    public UserService(UserDao dao, GroupLectureDao lectureDao, GroupLectureService lectureService, PasswordEncoder passwordEncoder) {
        this.dao = dao;
        this.lectureDao = lectureDao;
        this.lectureService = lectureService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void persist(User user) {
        Objects.requireNonNull(user);
        user.encodePassword(passwordEncoder);
        if (user.getRole() == null) {
            user.setRole(Constants.DEFAULT_ROLE);
        }
        dao.persist(user);
    }

    @Transactional(readOnly = true)
    public boolean exists(String email) {
        return dao.findByEmail(email) != null;
    }

    @Transactional(readOnly = true)
    public List<User> findAll(GroupLecture groupLecture) {
        return dao.findAll(groupLecture);
    }

    @Transactional
    public void update(User user) {
        Objects.requireNonNull(user);
        dao.update(user);
    }

    @Transactional(readOnly = true)
    public User find(Integer id) {
        return dao.find(id);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return dao.findByEmail(email);
    }

    //user registers for an existing course in the schedule
    @Transactional
    public void registerForLecture(GroupLecture groupLecture, User user) {
        Objects.requireNonNull(groupLecture);
        Objects.requireNonNull(user);
        if (capacityIsEnoughForSignUp(groupLecture) && user.getCard().getValid()) {
            user.registerForLecture(groupLecture);
            groupLecture.setOccupancy(groupLecture.getOccupancy() + 1);
            lectureDao.update(groupLecture);
        } else if (capacityIsEnoughForSignUp(groupLecture) && userBalanceIsEnough(groupLecture.getPrice(), user)) {
            user.registerForLecture(groupLecture);
            lectureService.payForGroupLecture(groupLecture, user);
            groupLecture.setOccupancy(groupLecture.getOccupancy() + 1);
            lectureDao.update(groupLecture);
        } else if (!capacityIsEnoughForSignUp(groupLecture)) {
            throw new CapacityIsNotEnoughException("The lecture's capacity is not enough for registering in this lecture");
        } else {
            throw new ClientBalanceIsNotEnough("The client's balance is not enough for registering in this lecture");
        }
    }

    //returns list of user with greater balance than is required
    @Transactional
    public List<User> getUsersWithGreaterBalance(BigDecimal balance) {
        return dao.findUsersWithGreaterBalance(balance);
    }
}

