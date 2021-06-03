package cz.cvut.kbss.ear.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cz.cvut.kbss.ear.environment.Generator;
import cz.cvut.kbss.ear.model.GroupLecture;
import cz.cvut.kbss.ear.model.Room;
import cz.cvut.kbss.ear.service.GroupLectureService;
import cz.cvut.kbss.ear.service.RoomService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RoomControllerGetTest extends BaseControllerTestRunner {
    @Mock
    private RoomService roomServiceMock;
    @Mock
    private GroupLectureService lectureServiceMock;
    @InjectMocks
    private RoomController sut;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        super.setUp(sut);
    }

    @Test
    public void getAllReturnsRoomsReadByCategoryService() throws Exception {
        final List<Room> rooms = IntStream.range(0, 5).mapToObj(i -> {
            final Room cat = new Room();
            cat.setName("Room" + i);
            cat.setId(Generator.randomInt());
            return cat;
        }).collect(Collectors.toList());
        when(roomServiceMock.findAll()).thenReturn(rooms);

        final MvcResult mvcResult = mockMvc.perform(get("/rest/rooms")).andReturn();
        final List<Room> result = readValue(mvcResult, new TypeReference<List<Room>>() {
        });

        assertEquals(rooms.size(), result.size());
        verify(roomServiceMock).findAll();
    }

    @Test
    public void getByIdReturnsMatchingRoom() throws Exception {
        final Room room = new Room();
        room.setId(Generator.randomInt());
        room.setName("room");
        when(roomServiceMock.find(room.getId())).thenReturn(room);

        final MvcResult mvcResult = mockMvc.perform(get("/rest/rooms/" + room.getId())).andReturn();
        final Room result = readValue(mvcResult, Room.class);

        assertNotNull(result);
        assertEquals(room.getId(), result.getId());
        assertEquals(room.getName(), result.getName());
    }

    @Test
    public void getGroupLecturesByRoomReturnsGroupLecturesForRoom() throws Exception {
        final List<GroupLecture> lectures = Arrays.asList(Generator.generateGroupLecture(), Generator.generateGroupLecture());
        when(lectureServiceMock.findAll(any())).thenReturn(lectures);
        final Room room = new Room();
        room.setId(Generator.randomInt());
        when(roomServiceMock.find(any())).thenReturn(room);

        final MvcResult mvcResult = mockMvc.perform(get("/rest/rooms/" + room.getId() + "/lectures")).andReturn();
        final List<GroupLecture> result = readValue(mvcResult, new TypeReference<List<GroupLecture>>() {
        });

        assertNotNull(result);
        assertEquals(lectures.size(), result.size());
        verify(roomServiceMock).find(room.getId());
        verify(lectureServiceMock).findAll(room);
    }

    @Test
    public void getGroupLecturesByRoomThrowsNotFoundForUnknownRoomId() throws Exception {
        final int id = 123;
        mockMvc.perform(get("/rest/rooms/" + id + "/lectures")).andExpect(status().isNotFound());
        verify(roomServiceMock).find(id);
        verify(lectureServiceMock, never()).findAll(any());
    }
}
