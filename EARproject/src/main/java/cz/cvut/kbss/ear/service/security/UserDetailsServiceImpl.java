package cz.cvut.kbss.ear.service.security;

import cz.cvut.kbss.ear.dao.UserDao;
import cz.cvut.kbss.ear.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {
   private final UserDao userDao;

   @Autowired
    public UserDetailsServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       final cz.cvut.kbss.ear.model.User user = userDao.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        return new cz.cvut.kbss.ear.security.model.UserDetails(user);
    }
}