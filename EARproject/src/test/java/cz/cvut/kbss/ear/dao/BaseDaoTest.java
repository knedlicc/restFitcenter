package cz.cvut.kbss.ear.dao;

import cz.cvut.kbss.ear.SportReservationSystemApplication;
import cz.cvut.kbss.ear.environment.Generator;
import cz.cvut.kbss.ear.exception.PersistenceException;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.model.SportCenter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
//@SpringBootTest(classes = SportReservationSystemApplication.class)
@DataJpaTest
@ContextConfiguration(classes = SportReservationSystemApplication.class)
@ComponentScan(basePackageClasses = SportReservationSystemApplication.class)
public class BaseDaoTest {


    @Autowired
    private TestEntityManager em;

    @Autowired
    private RoomDao sut;

    @Test
    public void persistSavesSpecifiedInstance() {
        final Room room = generateRoom();
        sut.persist(room);
        assertNotNull(room.getId());

        final Room result = em.find(Room.class, room.getId());
        assertNotNull(result);
        assertEquals(room.getId(), result.getId());
        assertEquals(room.getName(), result.getName());
    }


    private static Room generateRoom() {
        final Room room = new Room();
        final SportCenter sc = Generator.generateSportCenter();
        room.setName("Test room " + Generator.randomInt());
        room.setSportCenter(sc);
        room.setCapacity(30);
        return room;
    }

    @Test
    public void findRetrievesInstanceByIdentifier() {
        final Room room = generateRoom();
        em.persistAndFlush(room);
        assertNotNull(room.getId());

        final Room result = sut.find(room.getId());
        assertNotNull(result);
        assertEquals(room.getId(), result.getId());
        assertEquals(room.getName(), result.getName());
    }

    @Test
    public void findAllRetrievesAllInstancesOfType() {
        final Room room = generateRoom();
        em.persistAndFlush(room);
        final Room room2 = generateRoom();
        em.persistAndFlush(room2);

        final List<Room> result = sut.findAll();
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(room.getId())));
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(room2.getId())));
    }

    @Test
    public void updateUpdatesExistingInstance() {
        final Room room = generateRoom();
        em.persistAndFlush(room);

        final Room update = new Room();
        update.setId(room.getId());
        final String newName = "New category name";
        update.setName(newName);
        sut.update(update);

        final Room result = sut.find(room.getId());
        assertNotNull(result);
        assertEquals(room.getName(), result.getName());
    }

    @Test
    public void removeRemovesSpecifiedInstance() {
        final Room room = generateRoom();
        em.persistAndFlush(room);
        assertNotNull(em.find(Room.class, room.getId()));
        em.detach(room);

        sut.remove(room);
        assertNull(em.find(Room.class, room.getId()));
    }

    @Test
    public void removeDoesNothingWhenInstanceDoesNotExist() {
        final Room room = generateRoom();
        room.setId(123);
        assertNull(em.find(Room.class, room.getId()));

        sut.remove(room);
        assertNull(em.find(Room.class, room.getId()));
    }

    @Test(expected = PersistenceException.class)
    public void exceptionOnPersistInWrappedInPersistenceException() {
        final Room room = generateRoom();
        em.persistAndFlush(room);
        em.remove(room);
        sut.update(room);
    }

    @Test
    public void existsReturnsTrueForExistingIdentifier() {
        final Room room = generateRoom();
        em.persistAndFlush(room);
        assertTrue(sut.exists(room.getId()));
        assertFalse(sut.exists(-1));
    }
}
