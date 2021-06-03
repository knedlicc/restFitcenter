package cz.cvut.kbss.ear.model;


import cz.cvut.kbss.ear.environment.Generator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SportCenterTest {

    @Test
    public void addRoomWorksWhenAddingRoomForFirstTime() {
        final SportCenter sportCenter = Generator.generateSportCenter();
        final Room room = new Room();
        room.setName("test");
        room.setId(Generator.randomInt());
        sportCenter.addRoom(room);

        assertEquals(1, sportCenter.getRooms().size());
    }

    @Test
    public void addRoomWorksForCenterWithExistingRoom() {
        final SportCenter sportCenter = Generator.generateSportCenter();
        final Room room1 = new Room();
        room1.setName("test");
        room1.setId(Generator.randomInt());
        sportCenter.setRooms(new ArrayList<>(Collections.singletonList(room1)));

        final Room room2 = new Room();
        room2.setName("test two");
        room2.setId(Generator.randomInt());

        sportCenter.addRoom(room2);
        assertEquals(2, sportCenter.getRooms().size());
    }
}
