package cz.cvut.kbss.ear.service;


import cz.cvut.kbss.ear.dao.GroupLectureDao;
import cz.cvut.kbss.ear.dao.UserDao;
import cz.cvut.kbss.ear.environment.Generator;
import cz.cvut.kbss.ear.model.Roles;
import cz.cvut.kbss.ear.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;


public class UserServiceTest {
    @Mock
    private UserDao userDaoMock;
    @Mock
    private GroupLectureDao lectureDaoMock;
    @Mock
    private UserService sut;
    @Mock
    private GroupLectureService lectureServiceMock;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.sut = new UserService(userDaoMock, lectureDaoMock, lectureServiceMock, passwordEncoder);
    }

    @Test
    public void persistEncodesUserPassword() {
        final User user = Generator.generateUser();
        final String rawPassword = user.getPassword();
        sut.persist(user);

        final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDaoMock).persist(captor.capture());
    }


    @Test
    public void persistSetsUserRoleToDefaultWhenItIsNotSpecified() {
        final User user = Generator.generateUser();
        user.setRole(null);
        sut.persist(user);

        final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDaoMock).persist(captor.capture());
        assertEquals(Roles.USER, captor.getValue().getRole());
    }


}


